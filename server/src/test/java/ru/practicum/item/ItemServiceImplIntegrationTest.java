package ru.practicum.item;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.User;
import ru.practicum.user.UserDto;
import ru.practicum.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void shouldAddNewItemSuccessfully() {
        UserDto owner = userService.addUser(createUserDto(null, "owner", "owner@email.com"));
        ItemDto newItem = createItemDto(null, "item1", "description1", null, true);

        ItemDto savedItem = itemService.addNew(owner.getId(), newItem);

        assertNotNull(savedItem.getId(), "Saved item should have an ID");
        assertEquals(newItem.getName(), savedItem.getName(), "Item name should match");
        assertEquals(newItem.getDescription(), savedItem.getDescription(), "Item description should match");
        assertEquals(owner.getId(), savedItem.getOwner().getId(), "Owner ID should match");
    }

    @Test
    void shouldUpdateItemSuccessfully() throws BadRequestException {
        UserDto owner = userService.addUser(createUserDto(null, "owner", "owner@email.com"));
        ItemDto newItem = itemService.addNew(owner.getId(), createItemDto(null, "item1",
                "description1", null, true));

        ItemDto updatedItem = new ItemDto();
        updatedItem.setName("updatedName");
        updatedItem.setDescription("updatedDescription");
        updatedItem.setAvailable(false);

        ItemDto result = itemService.update(owner.getId(), newItem.getId(), updatedItem);

        assertEquals("updatedName", result.getName(), "Item name should be updated");
        assertEquals("updatedDescription", result.getDescription(), "Item description should be updated");
        assertFalse(result.getAvailable(), "Item availability should be updated");
    }

    @Test
    void shouldGetAllUserItemsSuccessfully() throws BadRequestException {
        UserDto owner = userService.addUser(createUserDto(null, "owner", "owner@email.com"));
        itemService.addNew(owner.getId(), createItemDto(null, "item1", "description1",
                null, true));
        itemService.addNew(owner.getId(), createItemDto(null, "item2", "description2",
                null, true));

        List<Item> items = itemService.getAll(owner.getId());

        assertEquals(2, items.size(), "Owner should have 2 items");
    }

    @Test
    void shouldThrowWhenUpdatingItemOwnedByAnotherUser() {
        UserDto owner = userService.addUser(createUserDto(null, "owner", "owner@email.com"));
        UserDto anotherUser = userService.addUser(createUserDto(null, "anotherUser", "another@email.com"));
        ItemDto newItem = itemService.addNew(owner.getId(), createItemDto(null, "item1", "description1",
                null, true));

        ItemDto updatedItem = new ItemDto();
        updatedItem.setName("updatedName");

        assertThrows(Exception.class, () -> {
            itemService.update(anotherUser.getId(), newItem.getId(), updatedItem);
        }, "Should throw exception when updating an item not owned by user");
    }

    @Test
    void shouldSearchItemsSuccessfully() throws BadRequestException {
        UserDto owner = userService.addUser(createUserDto(null, "owner", "owner@email.com"));
        itemService.addNew(owner.getId(), createItemDto(null, "Hammer", "Useful tool", null,
                true));
        itemService.addNew(owner.getId(), createItemDto(null, "Wrench", "Another tool", null,
                true));

        List<Item> searchResults = itemService.search("tool");

        assertEquals(2, searchResults.size(), "Should find 2 items matching search");
    }

    private ItemDto createItemDto(Long id, String name, String description, User owner, Boolean available) {
        ItemDto item = new ItemDto();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setOwner(owner);
        item.setAvailable(available);
        return item;
    }

    private UserDto createUserDto(Long id, String name, String email) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}