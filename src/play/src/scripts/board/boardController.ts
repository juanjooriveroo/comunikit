// Controlador principal del tablero
import type { Board, SectionPosition, PictogramPositionBoard, SelectedPictogram } from './types';
import { GRID_COLS, GRID_ROWS } from './types';
import { boardState } from './boardState';
import { speakPhrase, speakText, ensureAudioUnlocked, cancelSpeech } from './speechService';

// Pictogramas default que van siempre en el tablero
function getDefaultPictograms(language: string = 'es'): PictogramPositionBoard[] {
  const translations: Record<string, Record<string, string>> = {
    es: {
      // Básicos (columnas 1-2, filas 1-7)
      'yo': 'Yo', 'el': 'Él', 'tu': 'Tú', 'ella': 'Ella', 'nosotros': 'Nosotros', 'ellos': 'Ellos',
      'vosotros': 'Vosotros', 'este': 'Este', 'mas': 'Más', 'menos': 'Menos', 'bien': 'Bien', 'mal': 'Mal',
      'si': 'Sí', 'no': 'No',
      // Nuevos (resto del tablero excepto esquina inferior derecha)
      'querer': 'Querer', 'gustar': 'Gustar', 'ir': 'Ir', 'dar': 'Dar', 'mucho': 'Mucho', 'diferente': 'Diferente',
      'tambien': 'También', 'muy': 'Muy', 'poner': 'Poner', 'necesitar': 'Necesitar', 'ser': 'Ser', 'sentir': 'Sentir',
      'dormir': 'Dormir', 'doler': 'Doler', 'contento': 'Contento', 'enfadado': 'Enfadado',
      'y': 'Y', 'hacer': 'Hacer', 'escuchar': 'Escuchar', 'pensar': 'Pensar', 'coger': 'Coger', 'a': 'A',
      'ver': 'Ver', 'estar': 'Estar', 'jugar': 'Jugar', 'tener': 'Tener', 'de': 'De', 'ahora': 'Ahora',
      'comer': 'Comer', 'beber': 'Beber', 'bano': 'Baño', 'con': 'Con', 'despues': 'Después', 'terminar': 'Terminar',
      'poder': 'Poder', 'un': 'Un', 'aqui': 'Aquí', 'ayer': 'Ayer', 'hoy': 'Hoy', 'manana': 'Mañana'
    },
    en: {
      // Básicos
      'yo': 'I', 'el': 'He', 'tu': 'You', 'ella': 'She', 'nosotros': 'We', 'ellos': 'They',
      'vosotros': 'You (plural)', 'este': 'This', 'mas': 'More', 'menos': 'Less', 'bien': 'Good', 'mal': 'Bad',
      'si': 'Yes', 'no': 'No',
      // Nuevos
      'querer': 'Want', 'gustar': 'Like', 'ir': 'Go', 'dar': 'Give', 'mucho': 'Much', 'diferente': 'Different',
      'tambien': 'Also', 'muy': 'Very', 'poner': 'Put', 'necesitar': 'Need', 'ser': 'Be', 'sentir': 'Feel',
      'dormir': 'Sleep', 'doler': 'Hurt', 'contento': 'Happy', 'enfadado': 'Angry',
      'y': 'And', 'hacer': 'Do', 'escuchar': 'Listen', 'pensar': 'Think', 'coger': 'Take', 'a': 'To',
      'ver': 'See', 'estar': 'Be', 'jugar': 'Play', 'tener': 'Have', 'de': 'Of', 'ahora': 'Now',
      'comer': 'Eat', 'beber': 'Drink', 'baño': 'Bathroom', 'con': 'With', 'despues': 'After', 'terminar': 'Finish',
      'poder': 'Can', 'un': 'A', 'aqui': 'Here', 'ayer': 'Yesterday', 'hoy': 'Today', 'mañana': 'Tomorrow'
    },
    fr: {
      // Básicos
      'yo': 'Je', 'el': 'Il', 'tu': 'Tu', 'ella': 'Elle', 'nosotros': 'Nous', 'ellos': 'Ils',
      'vosotros': 'Vous', 'este': 'Ce', 'mas': 'Plus', 'menos': 'Moins', 'bien': 'Bien', 'mal': 'Mal',
      'si': 'Oui', 'no': 'Non',
      // Nuevos
      'querer': 'Vouloir', 'gustar': 'Aimer', 'ir': 'Aller', 'dar': 'Donner', 'mucho': 'Beaucoup', 'diferente': 'Différent',
      'tambien': 'Aussi', 'muy': 'Très', 'poner': 'Mettre', 'necesitar': 'Avoir besoin', 'ser': 'Être', 'sentir': 'Sentir',
      'dormir': 'Dormir', 'doler': 'Faire mal', 'contento': 'Content', 'enfadado': 'Fâché',
      'y': 'Et', 'hacer': 'Faire', 'escuchar': 'Écouter', 'pensar': 'Penser', 'coger': 'Prendre', 'a': 'À',
      'ver': 'Voir', 'estar': 'Être', 'jugar': 'Jouer', 'tener': 'Avoir', 'de': 'De', 'ahora': 'Maintenant',
      'comer': 'Manger', 'beber': 'Boire', 'baño': 'Toilettes', 'con': 'Avec', 'despues': 'Après', 'terminar': 'Terminer',
      'poder': 'Pouvoir', 'un': 'Un', 'aqui': 'Ici', 'ayer': 'Hier', 'hoy': 'Aujourd\'hui', 'mañana': 'Demain'
    },
    de: {
      // Básicos
      'yo': 'Ich', 'el': 'Er', 'tu': 'Du', 'ella': 'Sie', 'nosotros': 'Wir', 'ellos': 'Sie (pl)',
      'vosotros': 'Ihr', 'este': 'Dieser', 'mas': 'Mehr', 'menos': 'Weniger', 'bien': 'Gut', 'mal': 'Schlecht',
      'si': 'Ja', 'no': 'Nein',
      // Nuevos
      'querer': 'Wollen', 'gustar': 'Mögen', 'ir': 'Gehen', 'dar': 'Geben', 'mucho': 'Viel', 'diferente': 'Anders',
      'tambien': 'Auch', 'muy': 'Sehr', 'poner': 'Setzen', 'necesitar': 'Brauchen', 'ser': 'Sein', 'sentir': 'Fühlen',
      'dormir': 'Schlafen', 'doler': 'Wehtun', 'contento': 'Froh', 'enfadado': 'Wütend',
      'y': 'Und', 'hacer': 'Machen', 'escuchar': 'Hören', 'pensar': 'Denken', 'coger': 'Nehmen', 'a': 'Zu',
      'ver': 'Sehen', 'estar': 'Sein', 'jugar': 'Spielen', 'tener': 'Haben', 'de': 'Von', 'ahora': 'Jetzt',
      'comer': 'Essen', 'beber': 'Trinken', 'baño': 'Toilette', 'con': 'Mit', 'despues': 'Nach', 'terminar': 'Beenden',
      'poder': 'Können', 'un': 'Ein', 'aqui': 'Hier', 'ayer': 'Gestern', 'hoy': 'Heute', 'mañana': 'Morgen'
    },
    pt: {
      // Básicos
      'yo': 'Eu', 'el': 'Ele', 'tu': 'Tu', 'ella': 'Ela', 'nosotros': 'Nós', 'ellos': 'Eles',
      'vosotros': 'Vós', 'este': 'Este', 'mas': 'Mais', 'menos': 'Menos', 'bien': 'Bem', 'mal': 'Mal',
      'si': 'Sim', 'no': 'Não',
      // Nuevos
      'querer': 'Querer', 'gustar': 'Gostar', 'ir': 'Ir', 'dar': 'Dar', 'mucho': 'Muito', 'diferente': 'Diferente',
      'tambien': 'Também', 'muy': 'Muito', 'poner': 'Pôr', 'necesitar': 'Precisar', 'ser': 'Ser', 'sentir': 'Sentir',
      'dormir': 'Dormir', 'doler': 'Doer', 'contento': 'Feliz', 'enfadado': 'Zangado',
      'y': 'E', 'hacer': 'Fazer', 'escuchar': 'Escutar', 'pensar': 'Pensar', 'coger': 'Pegar', 'a': 'A',
      'ver': 'Ver', 'estar': 'Estar', 'jugar': 'Jogar', 'tener': 'Ter', 'de': 'De', 'ahora': 'Agora',
      'comer': 'Comer', 'beber': 'Beber', 'baño': 'Banheiro', 'con': 'Com', 'despues': 'Depois', 'terminar': 'Terminar',
      'poder': 'Poder', 'un': 'Um', 'aqui': 'Aqui', 'ayer': 'Ontem', 'hoy': 'Hoje', 'mañana': 'Amanhã'
    }
  };

  const currentTranslations = translations[language] || translations['es'];

  const pictograms: PictogramPositionBoard[] = [];

  // ÁREA DE PICTOGRAMAS DEFAULT - TODO EXCEPTO ESQUINA INFERIOR DERECHA (cols 7-11, rows 1-6)
  
  // Fila 1 (row 0): columnas 0-6 + 7-11 (toda la fila)
  const row0 = [
    { key: 'yo', col: 0 }, { key: 'el', col: 1 }, { key: 'querer', col: 2 }, { key: 'gustar', col: 3 },
    { key: 'ir', col: 4 }, { key: 'dar', col: 5 }, { key: 'mucho', col: 6 }, { key: 'diferente', col: 7 },
    { key: 'terminar', col: 8 }, { key: 'tambien', col: 9 }, { key: 'contento', col: 10 }, { key: 'enfadado', col: 11 }
  ];
  
  // Fila 2 (row 1): columnas 0-6 (reservar 7-11 para petición)
  const row1 = [
    { key: 'tu', col: 0 }, { key: 'ella', col: 1 }, { key: 'poner', col: 2 }, { key: 'necesitar', col: 3 },
    { key: 'ser', col: 4 }, { key: 'sentir', col: 5 }, { key: 'y', col: 6, textOnly: true }
  ];
  
  // Fila 3 (row 2): columnas 0-6
  const row2 = [
    { key: 'nosotros', col: 0 }, { key: 'ellos', col: 1 }, { key: 'hacer', col: 2 }, { key: 'escuchar', col: 3 },
    { key: 'pensar', col: 4 }, { key: 'coger', col: 5 }, { key: 'a', col: 6, textOnly: true }
  ];
  
  // Fila 4 (row 3): columnas 0-6
  const row3 = [
    { key: 'vosotros', col: 0 }, { key: 'este', col: 1 }, { key: 'ver', col: 2 }, { key: 'estar', col: 3 },
    { key: 'jugar', col: 4 }, { key: 'tener', col: 5 }, { key: 'de', col: 6, textOnly: true }
  ];
  
  // Fila 5 (row 4): columnas 0-6
  const row4 = [
    { key: 'mas', col: 0 }, { key: 'menos', col: 1 }, { key: 'ahora', col: 2 }, { key: 'comer', col: 3 },
    { key: 'beber', col: 4 }, { key: 'bano', col: 5 }, { key: 'con', col: 6, textOnly: true }
  ];
  
  // Fila 6 (row 5): columnas 0-6
  const row5 = [
    { key: 'bien', col: 0 }, { key: 'mal', col: 1 }, { key: 'despues', col: 2 }, { key: 'dormir', col: 3 },
    { key: 'poder', col: 4 }, { key: 'doler', col: 5 }, { key: 'un', col: 6, textOnly: true }
  ];
  
  // Fila 7 (row 6): columnas 0-6
  const row6 = [
    { key: 'si', col: 0 }, { key: 'no', col: 1 }, { key: 'aqui', col: 2 }, { key: 'ayer', col: 3 },
    { key: 'hoy', col: 4 }, { key: 'manana', col: 5 }, { key: 'muy', col: 6, textOnly: true }
  ];

  const allRows = [row0, row1, row2, row3, row4, row5, row6];

  allRows.forEach((rowData, rowIndex) => {
    rowData.forEach(({ key, col, textOnly }: { key: string; col: number; textOnly?: boolean }) => {
      pictograms.push({
        pictogramId: `default-${key}`,
        name: currentTranslations[key],
        imageUrl: textOnly ? '' : `/pictogramas/${key}.png`,
        col: col,
        row: rowIndex
      });
    });
  });

  return pictograms;
}

// Elementos del DOM
let boardLoading: HTMLDivElement;
let boardError: HTMLDivElement;
let boardGrid: HTMLDivElement;
let phraseContent: HTMLDivElement;
let btnSpeak: HTMLButtonElement;
let btnClear: HTMLButtonElement;
let sectionModal: HTMLDivElement;
let sectionTitle: HTMLHeadingElement;
let sectionGrid: HTMLDivElement;
let modalClose: HTMLButtonElement;
let modalSectionImage: HTMLDivElement;

export function initBoard(): void {
  // Obtener elementos del DOM
  boardLoading = document.getElementById('board-loading') as HTMLDivElement;
  boardError = document.getElementById('board-error') as HTMLDivElement;
  boardGrid = document.getElementById('board-grid') as HTMLDivElement;
  phraseContent = document.getElementById('phrase-content') as HTMLDivElement;
  btnSpeak = document.getElementById('btn-speak') as HTMLButtonElement;
  btnClear = document.getElementById('btn-clear') as HTMLButtonElement;
  sectionModal = document.getElementById('section-modal') as HTMLDivElement;
  sectionTitle = document.getElementById('section-title') as HTMLHeadingElement;
  sectionGrid = document.getElementById('section-grid') as HTMLDivElement;
  modalClose = document.getElementById('modal-close') as HTMLButtonElement;
  modalSectionImage = document.getElementById('modal-section-image') as HTMLDivElement;

  // Configurar event listeners
  setupEventListeners();

  // Suscribirse a cambios de estado
  boardState.subscribe(() => {
    updatePhraseDisplay();
    updateButtonStates();
  });

  // Cargar datos del tablero
  loadBoardData();
}

function setupEventListeners(): void {
  btnSpeak.addEventListener('click', handleSpeakPhrase);
  btnClear.addEventListener('click', handleClear);
  modalClose.addEventListener('click', closeSectionModal);
  
  sectionModal.querySelector('.modal-backdrop')?.addEventListener('click', closeSectionModal);
  
  document.addEventListener('keydown', (e: KeyboardEvent) => {
    if (e.key === 'Escape' && sectionModal.getAttribute('aria-hidden') === 'false') {
      closeSectionModal();
    }
  });
}

function loadBoardData(): void {
  const storedBoard = localStorage.getItem('guestBoard');
  const storedLanguage = localStorage.getItem('guestLanguage') || 'es';

  if (!storedBoard || !storedLanguage) {
    showError();
    return;
  }

  try {
    const board: Board = JSON.parse(storedBoard);
    // Agregar pictogramas default al board
    const defaultPictograms = getDefaultPictograms(storedLanguage);
    if (!board.pictograms) {
      board.pictograms = [];
    }

    // Usar únicamente pictogramas top-level del board (no extraer de secciones)
    const sourcePictograms: PictogramPositionBoard[] = board.pictograms || [];
    // Mapear pictogramas de petición a la esquina inferior derecha

    // Detectar formato de coordenadas de los pictogramas top-level
    let adjustedPictograms: PictogramPositionBoard[] = [];
    if (sourcePictograms.length > 0) {
      const cols = sourcePictograms.map(sp => (typeof sp.col === 'number' ? sp.col : -1));
      const rows = sourcePictograms.map(sp => (typeof sp.row === 'number' ? sp.row : -1));
      const maxCol = Math.max(...cols);
      const maxRow = Math.max(...rows);
      const seemsMainGrid = maxCol <= GRID_COLS - 1 && maxRow <= GRID_ROWS - 1 && (maxCol > 4 || maxRow > 5);
      const seemsSubGrid5x6 = maxCol <= 4 && maxRow <= 5;
      console.debug('Detected pictogram coords', { maxCol, maxRow, seemsMainGrid, seemsSubGrid5x6 });

      // Asignación determinista: intentar usar coordenadas si encajan, si colisionan con secciones
      // buscar la primera celda libre en la esquina inferior derecha (cols 7-11, rows 1-6)
      const occupiedKeys = new Set<string>();
      if (board.sections) {
        board.sections.forEach(s => occupiedKeys.add(`${s.col}-${s.row}`));
      }

      const targetSlots: { col: number; row: number }[] = [];
      for (let r = 1; r <= 6; r++) {
        for (let c = 7; c <= 11; c++) {
          targetSlots.push({ col: c, row: r });
        }
      }

      const usedKeys = new Set<string>(occupiedKeys);
      adjustedPictograms = [];

      sourcePictograms.forEach((p, index) => {
        let desiredCol: number | undefined;
        let desiredRow: number | undefined;

        if (typeof p.col === 'number' && typeof p.row === 'number') {
          if (seemsMainGrid) {
            desiredCol = Math.min(Math.max(0, p.col), GRID_COLS - 1);
            desiredRow = Math.min(Math.max(0, p.row), GRID_ROWS - 1);
          } else if (seemsSubGrid5x6) {
            desiredCol = Math.min(Math.max(0, 7 + p.col), GRID_COLS - 1);
            desiredRow = Math.min(Math.max(0, 1 + p.row), GRID_ROWS - 1);
          }
        }

        // Fallback inicial si no hay coords válidas
        if (desiredCol === undefined || desiredRow === undefined) {
          desiredCol = 7 + (index % 5);
          desiredRow = 1 + Math.floor(index / 5);
        }

        const desiredKey = `${desiredCol}-${desiredRow}`;

        if (!usedKeys.has(desiredKey)) {
          usedKeys.add(desiredKey);
          adjustedPictograms.push({ ...p, col: desiredCol, row: desiredRow });
        } else {
          // Encontrar la primera celda libre dentro de targetSlots
          const free = targetSlots.find(slot => !usedKeys.has(`${slot.col}-${slot.row}`));
          if (free) {
            const freeKey = `${free.col}-${free.row}`;
            usedKeys.add(freeKey);
            console.debug(`Collision at ${desiredKey}, placing pictogram in free slot ${freeKey}`);
            adjustedPictograms.push({ ...p, col: free.col, row: free.row });
          } else {
            // Si no queda sitio en la esquina, usar el desired (aunque esté ocupado) o fallback fuera de área
            console.warn(`No free target slot available for pictogram index ${index}; assigning desired ${desiredKey}`);
            adjustedPictograms.push({ ...p, col: desiredCol, row: desiredRow });
          }
        }
      });
    }

    // Información mínima para debugging
    console.debug('Adjusted pictograms count', adjustedPictograms.length);

    board.pictograms = [...defaultPictograms, ...adjustedPictograms];

    const bottomRightCount = board.pictograms.filter(p => typeof p.col === 'number' && typeof p.row === 'number' && p.col >= 7 && p.row >= 1).length;
    console.debug(`Pictograms mapped to bottom-right: ${bottomRightCount}`);
    boardState.setBoardData(board);
    renderBoard(board);
  } catch (e) {
    console.error('Error parsing board data:', e);
    showError();
  }
}

function showError(): void {
  boardLoading.style.display = 'none';
  boardError.setAttribute('aria-hidden', 'false');
}

function renderBoard(board: Board | null): void {
  if (!board) {
    showError();
    return;
  }

  boardLoading.style.display = 'none';
  boardGrid.setAttribute('aria-hidden', 'false');
  boardGrid.innerHTML = '';

  // Crear mapa de posiciones ocupadas
  const occupiedCells = new Map<string, { type: 'section' | 'pictogram', data: SectionPosition | PictogramPositionBoard }>();

  // Mapear secciones
  if (board.sections) {
    // Detectar si las secciones vienen en el grid 5x6 (cols 0-4, rows 0-5)
    const maxSectionCol = Math.max(...board.sections.map(s => (typeof s.col === 'number' ? s.col : -1)));
    const maxSectionRow = Math.max(...board.sections.map(s => (typeof s.row === 'number' ? s.row : -1)));
    const sectionsSeemSubGrid = maxSectionCol <= 4 && maxSectionRow <= 5;
    if (sectionsSeemSubGrid) {
      console.debug('Mapping sections from 5x6 into bottom-right');
    }

    board.sections.forEach(section => {
      let placeCol = section.col;
      let placeRow = section.row;
      if (sectionsSeemSubGrid && typeof section.col === 'number' && typeof section.row === 'number') {
        placeCol = 7 + section.col; // mapear 0..4 -> 7..11
        placeRow = 1 + section.row; // mapear 0..5 -> 1..6
      }
      const key = `${placeCol}-${placeRow}`;
      // Crear una copia ligera con coords mapeadas para renderizar, sin mutar el objeto original
      const sectionForRender = { ...section, col: placeCol, row: placeRow } as SectionPosition;
      occupiedCells.set(key, { type: 'section', data: sectionForRender });
    });
  }

  // Mapear pictogramas sueltos
  if (board.pictograms) {
    board.pictograms.forEach(pictogram => {
      const key = `${pictogram.col}-${pictogram.row}`;
      // No sobrescribir una sección ya posicionada
      if (!occupiedCells.has(key)) {
        occupiedCells.set(key, { type: 'pictogram', data: pictogram });
      } else {
        // no sobrescribir secciones
      }
    });
  }

  // Generar grid 12x7
  // - Pictogramas default: todo el tablero EXCEPTO esquina inferior derecha
  // - Esquina inferior derecha (cols 7-11, rows 1-6): reservada para pictogramas de petición
  for (let row = 0; row < GRID_ROWS; row++) {
    for (let col = 0; col < GRID_COLS; col++) {
      const cell = document.createElement('div');
      cell.className = 'grid-cell';
      cell.dataset.col = col.toString();
      cell.dataset.row = row.toString();

      const key = `${col}-${row}`;
      const cellData = occupiedCells.get(key);

      // celdas generadas

      if (cellData) {
        if (cellData.type === 'section') {
          renderSectionCell(cell, cellData.data as SectionPosition);
        } else {
          renderPictogramCell(cell, cellData.data as PictogramPositionBoard);
        }
      } else {
        cell.classList.add('empty');
      }

      boardGrid.appendChild(cell);
    }
  }
}

function renderSectionCell(cell: HTMLElement, section: SectionPosition): void {
  cell.classList.add('section');
  
  if (section.imageUrl) {
    const img = document.createElement('img');
    img.className = 'cell-image';
    img.src = section.imageUrl;
    img.alt = section.name;
    img.loading = 'lazy';
    cell.appendChild(img);
  }

  const label = document.createElement('span');
  label.className = 'cell-label';
  label.textContent = section.name;
  cell.appendChild(label);

  cell.addEventListener('click', () => openSectionModal(section));
}

function renderPictogramCell(cell: HTMLElement, pictogram: PictogramPositionBoard): void {
  cell.classList.add('pictogram');
  
  if (pictogram.imageUrl) {
    const img = document.createElement('img');
    img.className = 'cell-image';
    img.src = pictogram.imageUrl;
    img.alt = pictogram.name;
    img.loading = 'lazy';
    cell.appendChild(img);
  } else {
    // Pictograma solo texto - hacer texto más grande
    cell.classList.add('text-only');
  }

  const label = document.createElement('span');
  label.className = 'cell-label';
  label.textContent = pictogram.name;
  cell.appendChild(label);

  cell.addEventListener('click', () => {
    // Reproducir audio del pictograma (conjugar si procede) y añadir a la frase
    try { ensureAudioUnlocked(); speakText(getSpokenForm(pictogram)); } catch (e) { /* noop */ }
    addToPhrase({ id: pictogram.pictogramId, name: pictogram.name, imageUrl: pictogram.imageUrl });
  });
}

function openSectionModal(section: SectionPosition): void {
  sectionTitle.textContent = section.name;
  sectionGrid.innerHTML = '';

  // Mostrar imagen de la sección en el header del modal
  if (section.imageUrl) {
    modalSectionImage.innerHTML = `<img src="${section.imageUrl}" alt="${section.name}" />`;
  } else {
    modalSectionImage.innerHTML = '📁';
  }

  // Crear mapa de pictogramas por posición
  const pictogramMap = new Map<string, any>();
  
  if (section.pictograms) {
    section.pictograms.forEach(p => {
      const key = `${p.col}-${p.row}`;
      pictogramMap.set(key, p);
    });
  }

  // Generar grid 5x6 para la sección
  for (let row = 0; row < 6; row++) {
    for (let col = 0; col < 5; col++) {
      const cell = document.createElement('div');
      cell.className = 'section-cell';
      
      const key = `${col}-${row}`;
      const pictogramPos = pictogramMap.get(key);

      if (pictogramPos && pictogramPos.pictogram) {
        const pictogram = pictogramPos.pictogram;
        
        const img = document.createElement('img');
        img.className = 'section-cell-image';
        img.src = pictogram.image?.imageUrl || '';
        img.alt = pictogram.name;
        img.loading = 'lazy';
        cell.appendChild(img);

        const label = document.createElement('span');
        label.className = 'section-cell-label';
        label.textContent = pictogram.name;
        cell.appendChild(label);

        cell.addEventListener('click', () => {
          try { ensureAudioUnlocked(); speakText(getSpokenFormFromSectionPictogram(pictogram)); } catch (e) { /* noop */ }
          addToPhrase({ id: pictogram.id, name: pictogram.name, imageUrl: pictogram.image?.imageUrl || '' });
          closeSectionModal();
        });
      } else {
        cell.classList.add('empty');
      }

      sectionGrid.appendChild(cell);
    }
  }

  sectionModal.setAttribute('aria-hidden', 'false');
  document.body.style.overflow = 'hidden';
}

function closeSectionModal(): void {
  sectionModal.setAttribute('aria-hidden', 'true');
  document.body.style.overflow = '';
}

function addToPhrase(pictogram: SelectedPictogram): void {
  boardState.addPictogram(pictogram);
  
  // Pequeña animación/feedback
  phraseContent.classList.add('pulse');
  setTimeout(() => phraseContent.classList.remove('pulse'), 200);
}

// Devuelve la forma hablada de un pictograma (conjugación en es si procede)
function getSpokenForm(p: PictogramPositionBoard): string {
  const lang = localStorage.getItem('guestLanguage') || 'es';
  const name = p.name || '';
  if (lang === 'es') {
    const conjugations: Record<string, string> = {
      'querer': 'quiero', 'gustar': 'me gusta', 'ir': 'voy', 'dar': 'doy', 'hacer': 'hago',
      'poner': 'pongo', 'necesitar': 'necesito', 'ser': 'soy', 'sentir': 'siento', 'ver': 'veo',
      'estar': 'estoy', 'jugar': 'juego', 'tener': 'tengo', 'comer': 'como', 'beber': 'bebo',
      'poder': 'puedo', 'escuchar': 'escucho', 'pensar': 'pienso', 'coger': 'cojo',
      'dormir': 'duermo', 'doler': 'me duele', 'contento': 'estoy contento', 'enfadado': 'estoy enfadado'
    };

    // intentar detectar la clave por pictogramId
    const id = (p.pictogramId || '').toLowerCase();
    const keys = Object.keys(conjugations).sort((a, b) => b.length - a.length);
    for (const k of keys) {
      if (id.includes(k)) return conjugations[k];
    }

    // fallback: comparar nombre renderizado (sin tildes)
    const normalize = (s: string) => s.normalize('NFD').replace(/\p{Diacritic}/gu, '').toLowerCase();
    let cleanName = name.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
    const keys2 = Object.keys(conjugations).sort((a, b) => b.length - a.length);
    for (const k of keys2) {
      if (cleanName.includes(k)) return conjugations[k];
    }
  }
  return p.name || '';
}

// Similar para pictogramas de sección (estructura diferente)
function getSpokenFormFromSectionPictogram(sp: any): string {
  const lang = localStorage.getItem('guestLanguage') || 'es';
  const name = sp.name || (sp.pictogram && sp.pictogram.name) || '';
  if (lang === 'es') {
    const conjugations: Record<string, string> = {
      'querer': 'quiero', 'gustar': 'me gusta', 'ir': 'voy', 'dar': 'doy', 'hacer': 'hago',
      'poner': 'pongo', 'necesitar': 'necesito', 'ser': 'soy', 'sentir': 'siento', 'ver': 'veo',
      'estar': 'estoy', 'jugar': 'juego', 'tener': 'tengo', 'comer': 'como', 'beber': 'bebo',
      'poder': 'puedo', 'escuchar': 'escucho', 'pensar': 'pienso', 'coger': 'cojo',
      'dormir': 'duermo', 'doler': 'me duele', 'contento': 'estoy contento', 'enfadado': 'estoy enfadado'
    };

    const id = (sp.pictogram && sp.pictogram.id ? sp.pictogram.id : (sp.id || '')).toLowerCase();
    const keys3 = Object.keys(conjugations).sort((a, b) => b.length - a.length);
    for (const k of keys3) {
      if (id.includes(k)) return conjugations[k];
    }
    const cleanName = name.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
    const keys4 = Object.keys(conjugations).sort((a, b) => b.length - a.length);
    for (const k of keys4) {
      if (cleanName.includes(k)) return conjugations[k];
    }
  }
  return name;
}

function updatePhraseDisplay(): void {
  const selectedPictograms = boardState.selectedPictograms;
  
  if (selectedPictograms.length === 0) {
    phraseContent.innerHTML = '<p class="phrase-placeholder">Toca los pictogramas para comunicarte</p>';
    return;
  }

  phraseContent.innerHTML = '';
  selectedPictograms.forEach((pictogram, index) => {
    const wordEl = document.createElement('div');
    wordEl.className = 'phrase-word';

    if (pictogram.imageUrl) {
      const imgEl = document.createElement('img');
      imgEl.src = pictogram.imageUrl;
      imgEl.alt = pictogram.name;
      wordEl.appendChild(imgEl);
    }

    const textEl = document.createElement('span');
    textEl.textContent = pictogram.name;
    wordEl.appendChild(textEl);

    // Click para eliminar
    wordEl.addEventListener('click', () => {
      boardState.removePictogram(index);
    });

    phraseContent.appendChild(wordEl);
  });
}

function updateButtonStates(): void {
  const hasContent = boardState.selectedPictograms.length > 0;
  btnSpeak.disabled = !hasContent;
  btnClear.disabled = !hasContent;
}

function handleClear(): void {
  boardState.clearPictograms();
  cancelSpeech();
}

function handleSpeakPhrase(): void {
  const pictograms = boardState.selectedPictograms;
  if (!pictograms || pictograms.length === 0) return;
  try {
    ensureAudioUnlocked();
  } catch (e) { /* noop */ }
  // Construir frase usando las formas habladas (conjugaciones cuando proceda)
  const parts = pictograms.map(p => {
    try {
      // SelectedPictogram tiene shape { id, name, imageUrl }
      // Reutilizar la función que maneja pictogramas de sección (acepta objetos con .id)
      return getSpokenFormFromSectionPictogram(p as any) || p.name || '';
    } catch (e) {
      return p.name || '';
    }
  }).filter(Boolean);

  const text = parts.join(' ');
  if (text) speakText(text);
}
