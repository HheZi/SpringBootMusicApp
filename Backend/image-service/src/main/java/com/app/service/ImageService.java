package com.app.service;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.app.model.RequestImage;

import lombok.SneakyThrows;

@Service
public class ImageService {

	@Value("${audio.path}")
	private String imagePath;
	
	@SneakyThrows
	public byte[] getImage(String name){
		return Files.readAllBytes(Path.of(imagePath, name));
	}
	
	@SneakyThrows
	public void saveImage(RequestImage image) {
		
		Files.write(Path.of(imagePath, image.getName()), image.getContent().getBytes(), CREATE_NEW);
	}
	
}
