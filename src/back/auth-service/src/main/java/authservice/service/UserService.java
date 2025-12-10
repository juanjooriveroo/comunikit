package authservice.service;

import authservice.dto.CreateUserRequestDto;
import authservice.dto.CreateUserResponseDto;
import authservice.dto.DependentAccountDto;
import authservice.dto.EditProfileRequestDto;
import authservice.dto.GetAllDependentsAccountsResponseDto;
import authservice.dto.StorageValidationResponseDto;
import authservice.entity.Language;
import authservice.entity.User;
import authservice.entity.UserRelation;
import authservice.exception.StorageLimitExceededException;
import authservice.exception.UserNotFoundException;
import authservice.exception.UserNotTutorException;
import authservice.mapper.UserMapper;
import authservice.repository.LanguageRepository;
import authservice.repository.UserRelationRepository;
import authservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRelationRepository userRelationRepository;
    private final UserMapper userMapper;
    private final LanguageRepository languageRepository;

    private float storageLimitMb = 50f;

    @Transactional
    public CreateUserResponseDto createUser(CreateUserRequestDto request, String userId) {
        User currentUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("Usuario actual no encontrado"));

        return switch (currentUser.getRol().getName()) {
            case "ADMIN" -> {
                User userTutor = userRepository.findById(request.userId())
                        .orElseThrow(() -> new UserNotFoundException("Usuario actual no encontrado"));

                if (!userTutor.getRol().getName().equals("TUTOR")) {
                    throw new UserNotTutorException("Usuario no válido por su rol");
                }

                yield createUserDependent(request, userTutor);
            }
            case "TUTOR" -> createUserDependent(request, currentUser);
            default -> throw new UserNotTutorException("Usuario no válido por su rol");
        };
    }

    @Transactional
    public void editProfile(String userId, EditProfileRequestDto request) {
        User currentUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("Usuario actual no encontrado"));

        User userToEdit;

        if (request.userId() != null){
            userToEdit = userRepository.findById(request.userId())
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        } else {
            userToEdit = currentUser;
        }

        switch (currentUser.getRol().getName()) {
            case "ADMIN" -> editAndSave(userToEdit, request);
            case "TUTOR" -> {
                boolean isSelf = userToEdit.getId().equals(currentUser.getId());
                boolean isDependentOfTutor = userToEdit.getTutors() != null && userToEdit.getTutors().getId().equals(currentUser.getId());
                if (!(isSelf || isDependentOfTutor)) {
                    throw new UserNotTutorException("Usuario no válido por su rol");
                }
                editAndSave(userToEdit, request);
            }
            default -> throw new UserNotTutorException("Usuario no válido por su rol");
        }
    }

    @Transactional
    public GetAllDependentsAccountsResponseDto getAllDependentsAccounts(String userID) {
        User currentUser = userRepository.findById(UUID.fromString(userID))
                .orElseThrow(() -> new UserNotFoundException("Usuario actual no encontrado"));

        GetAllDependentsAccountsResponseDto response = new GetAllDependentsAccountsResponseDto(new ArrayList<>());
        currentUser.getDependents().forEach(dependentAccount -> response.accounts().add(userMapper.toDtoDependentList(dependentAccount)));
        return response;
    }

    @Transactional
    public DependentAccountDto getDependentAccount(String userID, String id) {
        User requestedUser = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException("Usuario actual no encontrado"));

        if (!requestedUser.getTutors().getId().equals(UUID.fromString(userID))) {
            throw new UserNotTutorException("Usuario no válido por su rol");
        }

        return userMapper.toDto(requestedUser);
    }

    @Transactional
    public boolean validateUserRelation(UUID tutorId, UUID dependentId) {
        if (tutorId.equals(dependentId)) {
            return false;
        }

        return userRelationRepository.existsByTutorIdAndUserId(tutorId, dependentId);
    }

    @Transactional
    public StorageValidationResponseDto validateStorage(UUID ownerId, long bytesToAdd) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        float currentUsed = user.getStorage_used() == null ? 0F : user.getStorage_used();
        float storageTotal = currentUsed + bytesToMb(bytesToAdd);

        if (storageTotal > storageLimitMb && bytesToAdd > 0) {
            throw new StorageLimitExceededException("Límite de almacenamiento excedido");
        }

        return new StorageValidationResponseDto(storageTotal <= storageLimitMb);
    }

    private CreateUserResponseDto createUserDependent(CreateUserRequestDto request, User currentUser) {
        User newUser = userMapper.toEntityFromCreateUserRequest(request);
        User savedUser = userRepository.save(newUser);

        UserRelation relation = UserRelation.builder()
                .user(savedUser)
                .tutor(currentUser)
                .build();
        userRelationRepository.save(relation);

        return CreateUserResponseDto.builder()
                .username(savedUser.getUsername())
                .idUser(savedUser.getId())
                .build();
    }

    private void editAndSave(User userToEdit, EditProfileRequestDto request) {
        if (request.email() != null) userToEdit.setEmail(request.email());
        if (request.name() != null) userToEdit.setName(request.name());
        if (request.language() != null) {
            Language language = languageRepository.findById(request.language())
                    .orElseThrow(() -> new RuntimeException("Idioma no encontrado"));
            userToEdit.setLanguage(language);
        }
        userRepository.save(userToEdit);
    }

    private float bytesToMb(long bytes) {
        return bytes / 1024F / 1024F;
    }
}
