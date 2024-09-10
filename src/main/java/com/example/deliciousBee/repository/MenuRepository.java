package com.example.deliciousBee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.menu.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long>{

}
