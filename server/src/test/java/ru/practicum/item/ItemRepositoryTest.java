package ru.practicum.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldFindAllByOwner() {
        User owner = new User();
        owner.setEmail("owner@example.com");
        userRepository.save(owner);

        Item item1 = new Item();
        item1.setOwner(owner);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setOwner(owner);
        itemRepository.save(item2);

        List<Item> items = itemRepository.findAllByOwner(owner);
        assertEquals(2, items.size());
    }
}
