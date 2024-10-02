package tech.buildrun.agregadorinvestimentos.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

import tech.buildrun.agregadorinvestimentos.controller.dto.CreateUserDto;
import tech.buildrun.agregadorinvestimentos.controller.dto.UpdateUserDto;
import tech.buildrun.agregadorinvestimentos.entity.User;
import tech.buildrun.agregadorinvestimentos.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Nested
    class createUser {
        /**
         * O método `shouldCreateAUserWithSuccess` testa a criação bem-sucedida de um usuário no serviço `UserService`.
         * Ele configura um objeto `User` e um `CreateUserDto` com dados de entrada, simula a operação de salvamento no repositório de usuários usando Mockito,
         * e chama o método `createUser` do serviço. Após a execução, o método verifica se o usuário foi criado corretamente,
         * comparando os valores do objeto capturado com os dados de entrada, e assegura que o resultado não é nulo.
         */
        @Test
        @DisplayName("Should create a user with success")
        void shouldCreateAUserWithSuccess() {
            // Arrange
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null
            );
            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());
            var input = new CreateUserDto(
                    "username",
                    "email@email.com",
                    "123"
            );

            // Act
            var output = userService.createUser(input);

            // Assert
            assertNotNull(output);

            var userCaptured = userArgumentCaptor.getValue();

            assertEquals(input.username(), userCaptured.getUsername());
            assertEquals(input.email(), userCaptured.getEmail());
            assertEquals(input.password(), userCaptured.getPassword());
        }

        /**
         * O método `shouldThrowExceptionWhenErrorOccurs` verifica se uma exceção é lançada corretamente quando ocorre um erro
         * durante a operação de salvamento de um usuário no repositório. Ele simula uma exceção ao tentar salvar um usuário
         * usando Mockito e, em seguida, assegura que a exceção `RuntimeException` é lançada ao chamar o método `createUser` do serviço.
         */
        @Test
        @DisplayName("Should throw exception when error occurs")
        void shouldThrowExceptionWhenErrorOccurs() {
            // Arrange
            doThrow(new RuntimeException()).when(userRepository).save(any());
            var input = new CreateUserDto(
                    "username",
                    "email@email.com",
                    "123"
            );

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.createUser(input));
        }
    }

    @Nested
    class getUserById {

        /**
         * O método `shouldGetUserByIdWithSuccessWhenOptionalIsPresent` verifica se o método `getUserById` do serviço `UserService`
         * retorna um usuário com sucesso quando o repositório encontra um usuário correspondente ao ID fornecido.
         * Ele configura um objeto `User`, simula a operação de busca no repositório de usuários usando Mockito,
         * e chama o método `getUserById` do serviço. Após a execução, o método assegura que o resultado está presente
         * e que o ID do usuário capturado corresponde ao ID fornecido.
         */
        @Test
        @DisplayName("Should get user by id with success when optional is present")
        void shouldGetUserByIdWithSuccessWhenOptionalIsPresent() {

            // Arrange
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null
            );

            doReturn(Optional.of(user))
                    .when(userRepository)
                    .findById(uuidArgumentCaptor.capture());

            // Act
            var output = userService.getUserById(user.getUserId().toString());

            // Assert
            assertTrue(output.isPresent());
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());
        }

        /**
         * O método `shouldGetUserByIdWithSuccessWhenOptionalIsEmpty` verifica se o método `getUserById` do serviço `UserService`
         * retorna um resultado vazio quando o repositório não encontra um usuário correspondente ao ID fornecido.
         * Ele simula a operação de busca no repositório de usuários usando Mockito e chama o método `getUserById` do serviço.
         * Após a execução, o método assegura que o resultado está vazio e que o ID capturado corresponde ao ID fornecido.
         */
        @Test
        @DisplayName("Should get user by id with success when optional is empty")
        void shouldGetUserByIdWithSuccessWhenOptionalIsEmpty() {

            // Arrange
            var userId = UUID.randomUUID();
            doReturn(Optional.empty())
                    .when(userRepository)
                    .findById(uuidArgumentCaptor.capture());

            // Act
            var output = userService.getUserById(userId.toString());

            // Assert
            assertTrue(output.isEmpty());
            assertEquals(userId, uuidArgumentCaptor.getValue());
        }
    }

    @Nested
    class listUsers {

        /**
         * O método `shouldReturnAllUsersWithSuccess` verifica se o método `listUsers` do serviço `UserService`
         * retorna todos os usuários com sucesso. Ele configura uma lista de usuários, simula a operação de busca
         * no repositório de usuários usando Mockito, e chama o método `listUsers` do serviço. Após a execução,
         * o método assegura que o resultado não é nulo e que o tamanho da lista retornada corresponde ao esperado.
         */
        @Test
        @DisplayName("Should return all users with success")
        void shouldReturnAllUsersWithSuccess() {

            // Arrange
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null
            );
            var userList = List.of(user);
            doReturn(userList)
                    .when(userRepository)
                    .findAll();

            // Act
            var output = userService.listUsers();

            // Assert
            assertNotNull(output);
            assertEquals(userList.size(), output.size());
        }
    }

    @Nested
    class deleteById {

        /**
         * O método `shouldDeleteUserWithSuccessWhenUserExists` verifica se o método `deleteById` do serviço `UserService`
         * deleta um usuário com sucesso quando o usuário existe. Ele simula a verificação da existência do usuário
         * e a operação de deleção no repositório de usuários usando Mockito, e chama o método `deleteById` do serviço.
         * Após a execução, o método assegura que o ID do usuário capturado corresponde ao ID fornecido e que os métodos
         * `existsById` e `deleteById` foram chamados uma vez cada.
         */
        @Test
        @DisplayName("Should delete user with success when user exists")
        void shouldDeleteUserWithSuccessWhenUserExists() {

            // Arrange
            doReturn(true)
                    .when(userRepository)
                    .existsById(uuidArgumentCaptor.capture());

            doNothing()
                    .when(userRepository)
                    .deleteById(uuidArgumentCaptor.capture());

            var userId = UUID.randomUUID();

            // Act
            userService.deleteById(userId.toString());

            // Assert
            var idList = uuidArgumentCaptor.getAllValues();
            assertEquals(userId, idList.get(0));
            assertEquals(userId, idList.get(1));

            verify(userRepository, times(1)).existsById(idList.get(0));
            verify(userRepository, times(1)).deleteById(idList.get(1));
        }

        /**
         * O método `shouldNotDeleteUserWhenUserNotExists` verifica se o método `deleteById` do serviço `UserService`
         * não deleta um usuário quando o usuário não existe. Ele simula a verificação da inexistência do usuário
         * no repositório de usuários usando Mockito, e chama o método `deleteById` do serviço. Após a execução,
         * o método assegura que o ID do usuário capturado corresponde ao ID fornecido e que o método `deleteById`
         * não foi chamado.
         */
        @Test
        @DisplayName("Should not delete user when user NOT exists")
        void shouldNotDeleteUserWhenUserNotExists() {

            // Arrange
            doReturn(false)
                    .when(userRepository)
                    .existsById(uuidArgumentCaptor.capture());
            var userId = UUID.randomUUID();

            // Act
            userService.deleteById(userId.toString());

            // Assert
            assertEquals(userId, uuidArgumentCaptor.getValue());

            verify(userRepository, times(1))
                    .existsById(uuidArgumentCaptor.getValue());

            verify(userRepository, times(0)).deleteById(any());
        }
    }

    @Nested
    class updateUserById {

        /**
         * O método `shouldUpdateUserByIdWhenUserExistsAndUsernameAndPasswordIsFilled` verifica se o método `updateUserById` do serviço `UserService`
         * atualiza um usuário com sucesso quando o usuário existe e os campos `username` e `password` estão preenchidos.
         * Ele configura um objeto `UpdateUserDto` com novos dados, simula a operação de busca e atualização no repositório de usuários usando Mockito,
         * e chama o método `updateUserById` do serviço. Após a execução, o método assegura que o ID do usuário capturado corresponde ao ID fornecido,
         * que os valores do objeto capturado foram atualizados corretamente, e que os métodos `findById` e `save` foram chamados uma vez cada.
         */
        @Test
        @DisplayName("Should update user by id when user exists and username and password is filled")
        void shouldUpdateUserByIdWhenUserExistsAndUsernameAndPasswordIsFilled() {

            // Arrange
            var updateUserDto = new UpdateUserDto(
                    "newUsername",
                    "newPassword"
            );
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null
            );
            doReturn(Optional.of(user))
                    .when(userRepository)
                    .findById(uuidArgumentCaptor.capture());
            doReturn(user)
                    .when(userRepository)
                    .save(userArgumentCaptor.capture());

            // Act
            userService.updateUserById(user.getUserId().toString(), updateUserDto);

            // Assert
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());

            var userCaptured = userArgumentCaptor.getValue();

            assertEquals(updateUserDto.username(), userCaptured.getUsername());
            assertEquals(updateUserDto.password(), userCaptured.getPassword());

            verify(userRepository, times(1))
                    .findById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(1)).save(user);
        }

        /**
         * O método `shouldNotUpdateUserWhenUserNotExists` verifica se o método `updateUserById` do serviço `UserService`
         * não atualiza um usuário quando o usuário não existe. Ele configura um objeto `UpdateUserDto` com novos dados,
         * simula a operação de busca no repositório de usuários usando Mockito, e chama o método `updateUserById` do serviço.
         * Após a execução, o método assegura que o ID do usuário capturado corresponde ao ID fornecido e que o método `save` não foi chamado.
        */
        @Test
        @DisplayName("Should not update user when user not exists")
        void shouldNotUpdateUserWhenUserNotExists() {

            // Arrange
            var updateUserDto = new UpdateUserDto(
                    "newUsername",
                    "newPassword"
            );
            var userId = UUID.randomUUID();
            doReturn(Optional.empty())
                    .when(userRepository)
                    .findById(uuidArgumentCaptor.capture());

            // Act
            userService.updateUserById(userId.toString(), updateUserDto);

            // Assert
            assertEquals(userId, uuidArgumentCaptor.getValue());

            verify(userRepository, times(1))
                    .findById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(0)).save(any());
        }
    }
}