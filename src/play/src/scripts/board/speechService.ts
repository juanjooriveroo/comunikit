// Servicio de síntesis de voz
import { LANG_CODES } from './types';
import { boardState } from './boardState';

export function speakText(text: string): void {
  if (!text) return;
  const lang = localStorage.getItem('guestLanguage') || 'es';
  const utterance = new SpeechSynthesisUtterance(text);
  utterance.lang = LANG_CODES[lang] || LANG_CODES['es'];
  utterance.rate = 0.9;
  utterance.pitch = 1;
  try {
    // Intentar asegurar reproducción en móviles: reanudar AudioContext si existe
    try {
      // @ts-ignore
      if (window && (window as any).audioCtx && (window as any).audioCtx.state === 'suspended') {
        // @ts-ignore
        (window as any).audioCtx.resume().catch(() => {});
      }
    } catch (e) {
      // ignore
    }
    if (typeof window === 'undefined' || !('speechSynthesis' in window)) {
      console.warn('speechSynthesis not available in this environment');
      return;
    }

    const PREFERRED_VARIANTS: Record<string, string[]> = {
      es: ['es-ES', 'es-419', 'es'],
      en: ['en-US', 'en-GB', 'en'],
      fr: ['fr-FR', 'fr'],
      de: ['de-DE', 'de'],
      pt: ['pt-PT', 'pt-BR', 'pt']
    };

    const trySpeak = () => {
      try {
        const voices = speechSynthesis.getVoices();
        const variants = PREFERRED_VARIANTS[lang] || [LANG_CODES[lang] || 'en-US'];
        // 1) buscar por lang exacto/startswith en el orden de variantes
        let voice = variants.reduce((found, v) => found || voices.find(vo => vo.lang && vo.lang.toLowerCase().startsWith(v.toLowerCase())), undefined as SpeechSynthesisVoice | undefined);
        // 2) si no hay match por lang, intentar buscar por palabras comunes en el nombre (Google, Male, Female, Standard)
        if (!voice && voices.length > 0) {
          const nameHints = [lang, 'google', 'standard', 'female', 'male', 'native'];
          voice = voices.find(vo => nameHints.some(h => vo.name && vo.name.toLowerCase().includes(h)));
        }
        // 3) fallback: primera disponible
        if (!voice && voices.length > 0) voice = voices[0];
        if (voice) utterance.voice = voice;
        speechSynthesis.cancel();
        speechSynthesis.speak(utterance);
      } catch (err) {
        console.warn('Failed to speak (inner)', err);
      }
    };

    const voices = speechSynthesis.getVoices();
    if (!voices || voices.length === 0) {
      // En algunos Android las voces se cargan asíncronamente; reintentar cuando cambien
      const onVoices = () => {
        trySpeak();
        window.speechSynthesis.removeEventListener('voiceschanged', onVoices);
      };
      window.speechSynthesis.addEventListener('voiceschanged', onVoices);
      // y reintentar tras breve timeout por si no se dispara el evento
      setTimeout(trySpeak, 500);
    } else {
      trySpeak();
    }
  } catch (e) {
    console.warn('Speech synthesis failed', e);
  }
}

export function ensureAudioUnlocked(): void {
  try {
    // Crear y reanudar un AudioContext para desbloquear reproducción en navegadores móviles
    // @ts-ignore
    if (!window || (window as any).audioCtx) return;
    // @ts-ignore
    const AudioCtx = window.AudioContext || window.webkitAudioContext;
    if (!AudioCtx) return;
    // @ts-ignore
    const ctx = new AudioCtx();
    // @ts-ignore
    (window as any).audioCtx = ctx;
    // Reanudar y tocar un breve tono imperceptible para desbloquear audio
    ctx.resume().then(() => {
      try {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.connect(gain);
        gain.connect(ctx.destination);
        gain.gain.value = 0; // silencioso, solo desbloquea
        osc.start();
        setTimeout(() => {
          try { osc.stop(); } catch (e) {}
        }, 50);
      } catch (e) {
        // ignore
      }
    }).catch(() => {});
  } catch (e) {
    // noop
  }
}

export function speakPhrase(): void {
  const pictograms = boardState.selectedPictograms;
  if (pictograms.length === 0) return;
  const text = pictograms.map(p => p.name).join(' ');
  speakText(text);
}

export function cancelSpeech(): void {
  try { speechSynthesis.cancel(); } catch (e) { /* noop */ }
}
