package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.ShippingAddressDTO;
import com.tomato.tomato_mall.dto.ShippingAddressUpdateDTO;
import com.tomato.tomato_mall.service.ShippingAddressService;
import com.tomato.tomato_mall.vo.ResponseVO;
import com.tomato.tomato_mall.vo.ShippingAddressVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingAddressControllerTest {

    @Mock
    private ShippingAddressService shippingAddressService;

    @InjectMocks
    private ShippingAddressController shippingAddressController;

    private ShippingAddressDTO createDTO;
    private ShippingAddressUpdateDTO updateDTO;
    private ShippingAddressVO addressVO;

    @BeforeEach
    void setUp() {
        createDTO = new ShippingAddressDTO();
        createDTO.setName("张三");
        createDTO.setPhone("13800138000");
        createDTO.setAddress("北京市朝阳区某某街道123号");
        createDTO.setPostalCode("100000");

        updateDTO = new ShippingAddressUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setName("李四");
        updateDTO.setPhone("13900139000");
        updateDTO.setAddress("上海市黄浦区某某路456号");
        updateDTO.setPostalCode("200000");

        addressVO = ShippingAddressVO.builder()
                .id(1L)
                .name("张三")
                .phone("13800138000")
                .address("北京市朝阳区某某街道123号")
                .postalCode("100000")
                .build();
    }

    private void mockSecurityContext(String username) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetUserAddresses_Success() {
        // --- Arrange ---
        mockSecurityContext("testuser");
        ShippingAddressVO address1 = ShippingAddressVO.builder()
                .id(1L)
                .name("张三")
                .phone("13800138000")
                .address("北京市朝阳区某某街道123号")
                .postalCode("100000")
                .build();
        ShippingAddressVO address2 = ShippingAddressVO.builder()
                .id(2L)
                .name("李四")
                .phone("13900139000")
                .address("上海市黄浦区某某路456号")
                .postalCode("200000")
                .build();
        List<ShippingAddressVO> addresses = Arrays.asList(address1, address2);
        when(shippingAddressService.getUserAddresses(eq("testuser"))).thenReturn(addresses);

        // --- Act ---
        ResponseEntity<ResponseVO<List<ShippingAddressVO>>> response = shippingAddressController.getUserAddresses();

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<List<ShippingAddressVO>> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(addresses, body.getData());
        assertEquals(2, body.getData().size());

        verify(shippingAddressService, times(1)).getUserAddresses(eq("testuser"));
    }

    @Test
    void testCreateAddress_Success() {
        // --- Arrange ---
        mockSecurityContext("testuser");
        when(shippingAddressService.createAddress(eq("testuser"), any(ShippingAddressDTO.class)))
                .thenReturn(addressVO);

        // --- Act ---
        ResponseEntity<ResponseVO<ShippingAddressVO>> response = shippingAddressController.createAddress(createDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<ShippingAddressVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(addressVO, body.getData());

        verify(shippingAddressService, times(1)).createAddress(eq("testuser"), eq(createDTO));
    }

    @Test
    void testUpdateAddress_Success() {
        // --- Arrange ---
        mockSecurityContext("testuser");
        ShippingAddressVO updatedAddress = ShippingAddressVO.builder()
                .id(1L)
                .name("李四")
                .phone("13900139000")
                .address("上海市黄浦区某某路456号")
                .postalCode("200000")
                .build();
        when(shippingAddressService.updateAddress(eq("testuser"), any(ShippingAddressUpdateDTO.class)))
                .thenReturn(updatedAddress);

        // --- Act ---
        ResponseEntity<ResponseVO<ShippingAddressVO>> response = shippingAddressController.updateAddress(updateDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<ShippingAddressVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(updatedAddress, body.getData());

        verify(shippingAddressService, times(1)).updateAddress(eq("testuser"), eq(updateDTO));
    }

    @Test
    void testDeleteAddress_Success() {
        // --- Arrange ---
        mockSecurityContext("testuser");
        Long addressId = 1L;
        doNothing().when(shippingAddressService).deleteAddress(eq("testuser"), eq(addressId));

        // --- Act ---
        ResponseEntity<ResponseVO<String>> response = shippingAddressController.deleteAddress(addressId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<String> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals("收货信息删除成功", body.getData());

        verify(shippingAddressService, times(1)).deleteAddress(eq("testuser"), eq(addressId));
    }
}
