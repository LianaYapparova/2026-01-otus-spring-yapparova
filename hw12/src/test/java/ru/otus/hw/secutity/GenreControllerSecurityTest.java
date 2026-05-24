package ru.otus.hw.secutity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controller.GenreController;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.GenreService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
@Import(SecurityConfiguration.class)
public class GenreControllerSecurityTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldRedirectToLoginWhenUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/genres"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldReturnGenresPageForAuthenticatedUser() throws Exception {

        mockMvc.perform(get("/genres")
                        .with(user("petrov")
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());
    }
}
