package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.ShippingAddressDTO;
import com.tomato.tomato_mall.dto.ShippingAddressUpdateDTO;
import com.tomato.tomato_mall.entity.ShippingAddress;
import com.tomato.tomato_mall.entity.User;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.ShippingAddressRepository;
import com.tomato.tomato_mall.repository.UserRepository;
import com.tomato.tomato_mall.vo.ShippingAddressVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingAddressServiceImplTest {

    @Mock
    private ShippingAddressRepository shippingAddressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShippingAddressServiceImpl shippingAddressService;

    private User user;
    private ShippingAddress shippingAddress;
    private ShippingAddressDTO shippingAddressDTO;
    private ShippingAddressUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");

        shippingAddress = new ShippingAddress();
        shippingAddress.setId(1L);
        shippingAddress.setUser(user);
        shippingAddress.setName("Test Receiver");
        shippingAddress.setPhone("13800138000");
        shippingAddress.setAddress("Test Address");
        shippingAddress.setPostalCode("100000");

        shippingAddressDTO = new ShippingAddressDTO();
        shippingAddressDTO.setName("New Receiver");
        shippingAddressDTO.setPhone("13900139000");
        shippingAddressDTO.setAddress("New Address");
        shippingAddressDTO.setPostalCode("200000");

        updateDTO = new ShippingAddressUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setName("Updated Receiver");
        updateDTO.setPhone("13700137000");
        updateDTO.setAddress("Updated Address");
        updateDTO.setPostalCode("300000");
    }

    // --- createAddress 方法测试 ---
    @Test
    void createAddress_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(shippingAddressRepository.save(any(ShippingAddress.class))).thenAnswer(invocation -> {
            ShippingAddress saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // --- Act ---
        ShippingAddressVO result = shippingAddressService.createAddress(user.getUsername(), shippingAddressDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(shippingAddressDTO.getName(), result.getName());
        assertEquals(shippingAddressDTO.getPhone(), result.getPhone());
        assertEquals(shippingAddressDTO.getAddress(), result.getAddress());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(shippingAddressRepository, times(1)).save(any(ShippingAddress.class));
    }

    @Test
    void createAddress_UserNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            shippingAddressService.createAddress(user.getUsername(), shippingAddressDTO);
        });

        assertEquals(ErrorTypeEnum.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(shippingAddressRepository, never()).save(any());
    }

    // --- updateAddress 方法测试 ---
    @Test
    void updateAddress_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(shippingAddressRepository.findById(updateDTO.getId())).thenReturn(Optional.of(shippingAddress));
        when(shippingAddressRepository.save(any(ShippingAddress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        ShippingAddressVO result = shippingAddressService.updateAddress(user.getUsername(), updateDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(updateDTO.getName(), result.getName());
        assertEquals(updateDTO.getPhone(), result.getPhone());
        assertEquals(updateDTO.getAddress(), result.getAddress());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(shippingAddressRepository, times(1)).findById(updateDTO.getId());
        verify(shippingAddressRepository, times(1)).save(shippingAddress);
    }

    @Test
    void updateAddress_AddressNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(shippingAddressRepository.findById(updateDTO.getId())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            shippingAddressService.updateAddress(user.getUsername(), updateDTO);
        });

        assertEquals(ErrorTypeEnum.SHIPPING_ADDRESS_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(shippingAddressRepository, times(1)).findById(updateDTO.getId());
        verify(shippingAddressRepository, never()).save(any());
    }

    // --- deleteAddress 方法测试 ---
    @Test
    void deleteAddress_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(shippingAddressRepository.findById(1L)).thenReturn(Optional.of(shippingAddress));

        // --- Act ---
        shippingAddressService.deleteAddress(user.getUsername(), 1L);

        // --- Assert ---
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(shippingAddressRepository, times(1)).findById(1L);
        verify(shippingAddressRepository, times(1)).delete(shippingAddress);
    }

    @Test
    void deleteAddress_AddressNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(shippingAddressRepository.findById(1L)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            shippingAddressService.deleteAddress(user.getUsername(), 1L);
        });

        assertEquals(ErrorTypeEnum.SHIPPING_ADDRESS_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(shippingAddressRepository, times(1)).findById(1L);
        verify(shippingAddressRepository, never()).delete(any());
    }

    // --- getUserAddresses 方法测试 ---
    @Test
    void getUserAddresses_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        List<ShippingAddress> addresses = new ArrayList<>();
        addresses.add(shippingAddress);
        when(shippingAddressRepository.findByUser(user)).thenReturn(addresses);

        // --- Act ---
        List<ShippingAddressVO> result = shippingAddressService.getUserAddresses(user.getUsername());

        // --- Assert ---
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(shippingAddress.getName(), result.get(0).getName());
        assertEquals(shippingAddress.getPhone(), result.get(0).getPhone());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(shippingAddressRepository, times(1)).findByUser(user);
    }

    @Test
    void getUserAddresses_UserNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            shippingAddressService.getUserAddresses(user.getUsername());
        });

        assertEquals(ErrorTypeEnum.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(shippingAddressRepository, never()).findByUser(any());
    }
}
