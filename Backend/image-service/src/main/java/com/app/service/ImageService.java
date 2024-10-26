package com.app.service;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.app.model.RequestImage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
public class ImageService {

	@Value("${image.path}")
	private String imagePath;

	@Value("${image.default}")
	private String defaultImagePath;
	
	@SneakyThrows
	public byte[] getImage(String name){
		return Files.readAllBytes(Path.of(imagePath, name));
	}

	@SneakyThrows
	public byte[] getDefaultImage() {
		return Files.readAllBytes(Path.of(defaultImagePath));
	}
	
	@SneakyThrows
	public void saveImage(RequestImage image) {
		Files.write(Path.of(imagePath, image.getName()), image.getContent(), CREATE_NEW);
	}
	
}
