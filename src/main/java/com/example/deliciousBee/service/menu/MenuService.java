package com.example.deliciousBee.service.menu;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.deliciousBee.model.menu.Menu;
import com.example.deliciousBee.repository.MenuRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MenuService {
	
	private final MenuRepository menuRepository;

	public Menu findMenuById(Long menuId) {
		Optional<Menu> menu = menuRepository.findById(menuId);
		return menu.orElse(null);
	}

}
