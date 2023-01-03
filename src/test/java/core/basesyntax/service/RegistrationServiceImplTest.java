package core.basesyntax.service;

import core.basesyntax.dao.StorageDao;
import core.basesyntax.dao.StorageDaoImpl;
import core.basesyntax.db.Storage;
import core.basesyntax.exception.ValidationException;
import core.basesyntax.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistrationServiceImplTest {
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_AGE = 18;
    private static final String VALID_PASSWORD = "123456";
    private static final String INVALID_PASSWORD = "12345";
    private static final String GREAT_VALID_PASSWORD = "1234567";
    private static RegistrationServiceImpl registrationService;
    private static StorageDao storageDao;

    @BeforeAll
    static void beforeAll() {
        registrationService = new RegistrationServiceImpl();
        storageDao = new StorageDaoImpl();
    }

    @BeforeEach
    void setUp() {
        User user1 = new User();
        user1.setLogin("Bob");
        user1.setAge(MIN_AGE);
        user1.setPassword(VALID_PASSWORD);
        User user2 = new User();
        user2.setLogin("Alice");
        user2.setAge(MIN_AGE);
        user2.setPassword(VALID_PASSWORD);
        storageDao.add(user1);
        storageDao.add(user2);
    }

    @AfterEach
    void tearDown() {
        Storage.people.clear();
    }

    @Test
    void register_addNullUser_NotOk() {
        User newUser = null;
        Assertions.assertThrows(ValidationException.class,
                () -> registrationService.register(newUser),
                "Enter user");
    }

    @Test
    void register_addNullLogin_NotOk() {
        User newUser = new User();
        newUser.setAge(MIN_AGE);
        newUser.setPassword(VALID_PASSWORD);
        newUser.setLogin(null);
        Assertions.assertThrows(ValidationException.class,
                () -> registrationService.register(newUser),
                "Enter login");
    }

    @Test
    void register_addNullAge_NotOk() {
        User newUser = new User();
        newUser.setAge(null);
        newUser.setPassword(VALID_PASSWORD);
        newUser.setLogin("John");
        Assertions.assertThrows(ValidationException.class,
                () -> registrationService.register(newUser),
                "Enter age");
    }

    @Test
    void register_addNullPassword_NotOk() {
        User newUser = new User();
        newUser.setAge(MIN_AGE);
        newUser.setPassword(null);
        newUser.setLogin("John");
        Assertions.assertThrows(ValidationException.class,
                () -> registrationService.register(newUser),
                "Enter password");
    }

    @Test
    void register_addInvalidUsersAge_NotOk() {
        User newUser = new User();
        newUser.setAge(MIN_AGE - 1);
        newUser.setPassword(VALID_PASSWORD);
        newUser.setLogin("John");
        Assertions.assertThrows(ValidationException.class,
                () -> registrationService.register(newUser),
                "Age have to be 18 or more");
    }

    @Test
    void register_addAgeGreaterThanMinAge_ok() {
        User newUser = new User();
        newUser.setAge(MIN_AGE + 1);
        newUser.setPassword(VALID_PASSWORD);
        newUser.setLogin("John");
        registrationService.register(newUser);
        User actual = storageDao.get(newUser.getLogin());
        Assertions.assertEquals(newUser, actual);
    }

    @Test
    void register_passwordLengthIsLessThanMinLength_Ok() {
        User newUser = new User();
        newUser.setAge(MIN_AGE);
        newUser.setPassword(INVALID_PASSWORD);
        newUser.setLogin("John");
        Assertions.assertThrows(ValidationException.class,
                () -> registrationService.register(newUser),
                "Password should be 6 or more symbols");
    }

    @Test
    void register_passwordLengthIsGreaterThanMinLength_Ok() {
        User newUser = new User();
        newUser.setAge(MIN_AGE);
        newUser.setPassword(GREAT_VALID_PASSWORD);
        newUser.setLogin("John");
        registrationService.register(newUser);
        User actual = storageDao.get(newUser.getLogin());
        Assertions.assertEquals(newUser, actual);
    }

    @Test
    void register_loginExists_NotOk() {
        User newUser = new User();
        newUser.setAge(MIN_AGE);
        newUser.setPassword(VALID_PASSWORD);
        newUser.setLogin("Bob");
        Assertions.assertThrows(ValidationException.class,
                () -> registrationService.register(newUser),
                "User is already exists");
    }

    @Test
    void register_loginNotExists_NotOk() {
        User newUser = new User();
        newUser.setAge(MIN_AGE);
        newUser.setPassword(VALID_PASSWORD);
        newUser.setLogin("John");
        registrationService.register(newUser);
        User actual = storageDao.get(newUser.getLogin());
        Assertions.assertEquals(newUser, actual);
    }
}
