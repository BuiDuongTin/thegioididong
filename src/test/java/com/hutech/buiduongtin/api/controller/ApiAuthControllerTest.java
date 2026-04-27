package com.hutech.buiduongtin.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hutech.buiduongtin.controller.GlobalControllerAdvice;
import com.hutech.buiduongtin.model.User;
import com.hutech.buiduongtin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ApiAuthController.class, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalControllerAdvice.class))
@AutoConfigureMockMvc(addFilters = false)
class ApiAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void shouldRegisterAndPersistSecurityContextInSession() throws Exception {
        User user = user("newuser", "newuser@example.com");
        Authentication authentication = new UsernamePasswordAuthenticationToken("newuser", null, Collections.emptyList());

        when(userService.findByUsername("newuser")).thenReturn(null, user);
        when(userService.existsByEmail("newuser@example.com")).thenReturn(false);
        doNothing().when(userService).save(any(User.class));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterPayload("newuser", "123456", "newuser@example.com", "0909"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sessionType").value("SESSION"))
                .andExpect(jsonPath("$.data.user.username").value("newuser"))
                .andReturn();

        HttpSession session = result.getRequest().getSession(false);
        assertNotNull(session);
        assertNotNull(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }

    @Test
    void shouldLoginAndPersistSecurityContextInSession() throws Exception {
        User user = user("demo", "demo@example.com");
        Authentication authentication = new UsernamePasswordAuthenticationToken("demo", null, Collections.emptyList());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userService.findByUsername(eq("demo"))).thenReturn(user);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginPayload("demo", "123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.username").value("demo"))
                .andReturn();

        HttpSession session = result.getRequest().getSession(false);
        assertNotNull(session);
        assertNotNull(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }

    private User user(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }

    private record RegisterPayload(String username, String password, String email, String phone) {
    }

    private record LoginPayload(String username, String password) {
    }
}
