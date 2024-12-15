package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private ItemController itemController;

    private ItemDto itemDto;
    private Long userId = 1L;
    private Long itemId = 100L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();

        itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Item 1");
        itemDto.setDescription("Description for item 1");
        itemDto.setAvailable(true);
    }

    @Test
    void testGetItem() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(itemId);
        item.setName("Item 1");

        when(itemService.getOne(eq(itemId), eq(userId))).thenReturn(Optional.of(item));

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Item 1"));

        verify(itemService, times(1)).getOne(eq(itemId), eq(userId));
    }

    @Test
    void testAddItem() throws Exception {
        when(itemService.addNew(eq(userId), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Item 1"));

        verify(itemService, times(1)).addNew(eq(userId), any(ItemDto.class));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.update(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Item 1"));

        verify(itemService, times(1)).update(eq(userId), eq(itemId), any(ItemDto.class));
    }

    @Test
    void testGetAllItems() throws Exception {
        Item item1 = new Item();
        item1.setId(itemId);
        item1.setName("Item 1");
        Item item2 = new Item();
        item2.setId(itemId + 1);
        item2.setName("Item 2");

        when(itemService.getAll(eq(userId))).thenReturn(Arrays.asList(item1, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[1].id").value(itemId + 1));

        verify(itemService, times(1)).getAll(eq(userId));
    }

    @Test
    void testSearchItems() throws Exception {
        Item item1 = new Item();
        item1.setId(itemId);
        item1.setName("Item 1");

        when(itemService.search(eq("item"))).thenReturn(Arrays.asList(item1));

        mockMvc.perform(get("/items/search")
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId));

        verify(itemService, times(1)).search(eq("item"));
    }

    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
