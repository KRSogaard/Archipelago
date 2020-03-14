package build.archipelago.packageservice.controllers;

import build.archipelago.packageservice.core.delegates.createPackage.CreatePackageDelegate;
import build.archipelago.packageservice.core.delegates.getPackage.GetPackageDelegate;
import build.archipelago.packageservice.core.delegates.getPackage.GetPackageDelegateResponse;
import build.archipelago.packageservice.common.exceptions.PackageExistsException;
import build.archipelago.packageservice.models.GetPackageResponse;
import build.archipelago.packageservice.utils.TestUtils;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.commons.lang3.CharEncoding;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PackagesController.class)
public class PackagesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetPackageDelegate getPackageDelegate;

    @MockBean
    private CreatePackageDelegate createPackageDelegate;

    private Gson gson;

    @Before
    public void setUp() {
        gson = new Gson();
    }

    @Test
    public void testCreateValidPackage() throws Exception {
        this.mockMvc.perform(createPackageRequest("packages.request.valid.json"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateValidPackageThatAlreadyExists() throws Exception {
        doThrow(new PackageExistsException("Archipelago-Test-Package")).when(createPackageDelegate).create(any());
        this.mockMvc.perform(createPackageRequest("packages.request.valid.json"))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void testCreateInvalidPackageNoName() throws Exception {
        this.mockMvc.perform(createPackageRequest("packages.request.invalid.noname.json"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateInvalidPackageNoDesc() throws Exception {
        this.mockMvc.perform(createPackageRequest("packages.request.invalid.nodesc.json"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetValidPackage() throws Exception {
        String packageName = "PackageA";
        Instant instant = Instant.now();
        String desc = "This is a description " + UUID.randomUUID();
        when(getPackageDelegate.get(argThat(new ArgumentMatcher<GetPackageDelegateRequest>() {
            @Override
            public boolean matches(GetPackageDelegateRequest argument) {
                return packageName.equalsIgnoreCase(argument.getName());
            }
        }))).thenReturn(Optional.of(GetPackageDelegateResponse.builder()
                .create(instant)
                .name(packageName)
                .description(desc)
                .build()));

        MvcResult result = this.mockMvc.perform(getPackageRequest(packageName))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertFalse(Strings.isNullOrEmpty(content));

        GetPackageResponse response = gson.fromJson(content, GetPackageResponse.class);
        assertEquals(instant.toEpochMilli(), response.getCreated());
        assertEquals(packageName, response.getName());
        assertEquals(desc, response.getDescription());
    }

    @Test
    public void testGetNonExistingPackage() throws Exception {
        String packageName = "PackageA";
        when(getPackageDelegate.get(argThat(new ArgumentMatcher<GetPackageDelegateRequest>() {
            @Override
            public boolean matches(GetPackageDelegateRequest argument) {
                return packageName.equalsIgnoreCase(argument.getName());
            }
        }))).thenReturn(Optional.empty());

        this.mockMvc.perform(getPackageRequest(packageName))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private MockHttpServletRequestBuilder createPackageRequest(String file) throws IOException {
        return post("/package")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(CharEncoding.UTF_8)
                .content(TestUtils.readResourceFile(file));

    }

    private MockHttpServletRequestBuilder getPackageRequest(String name) {
        return get("/package/" + name);

    }
}