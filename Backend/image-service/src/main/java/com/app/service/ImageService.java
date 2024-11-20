package com.app.service;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.app.payload.RequestImage;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class ImageService {

	@Value("${image.path}")
	private String imagePath;

	@Value("${image.default}")
	private String defaultImagePath;
	
	private final ResourceLoader loader;
	
	@SneakyThrows
	public Resource getImage(String name){
		return new FileSystemResource(new File(imagePath, name));
	}

	@SneakyThrows
	public Resource getDefaultImage() {
		return new FileSystemResource(new File(defaultImagePath));
	}
	
	@SneakyThrows
	public void saveImage(RequestImage image) {
		Files.write(Path.of(imagePath, image.getName()), image.getContent());
	}
	
	@SneakyThrows
	public void deleteImage(String name) {
		Files.delete(Path.of(imagePath, name));
	}
	
}
