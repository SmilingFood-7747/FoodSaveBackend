package com.foodsave.platform.iam.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodsave.platform.iam.application.IamService;
import com.foodsave.platform.iam.domain.model.Account;
import com.foodsave.platform.iam.domain.model.AccountRole;
import com.foodsave.platform.iam.infrastructure.security.BearerTokenFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IamController.class)
@AutoConfigureMockMvc(addFilters = false)
class IamControllerWebTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IamService iam;

    @MockBean
    private BearerTokenFilter tokenFilter;

    @Test
    void createsCustomerAccountAndReturnsPublicAccountFields() throws Exception {
        var request = new SignUpRequest("Ana Pérez", "ana@example.com", "999111222", AccountRole.CUSTOMER, "FoodSave123!");
        var account = new Account("Ana Pérez", "ana@example.com", "999111222", AccountRole.CUSTOMER, "pbkdf2$hash");
        when(iam.register(any(SignUpRequest.class))).thenReturn(account);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Ana Pérez"))
                .andExpect(jsonPath("$.email").value("ana@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        verify(iam).register(any(SignUpRequest.class));
    }

    @Test
    void rejectsSignupWithInvalidEmail() throws Exception {
        var request = new SignUpRequest("Ana Pérez", "invalid-email", "999111222", AccountRole.CUSTOMER, "FoodSave123!");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());
    }
}
