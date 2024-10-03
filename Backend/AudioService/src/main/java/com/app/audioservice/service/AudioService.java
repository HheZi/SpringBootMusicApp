package com.app.audioservice.service;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.SneakyThrows;

@Service
public class AudioService {

	private static final String AUDIO_PATH =  "classpath:/audio/%s.mp3";
	
	@Autowired
	private ResourceLoader resourceLoader;

	@SneakyThrows
	public byte[] getResource(String filename, String rangeHeader) {
		Resource resource = resourceLoader.getResource(String.format(AUDIO_PATH, filename));
		
		List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
		long contentLength = resource.contentLength();
		
		byte[] res = new byte[] {};
		
		for (HttpRange httpRange : ranges) {
			
			int rangeStart = (int) httpRange.getRangeStart(contentLength);
			int rangeEnd = (int) httpRange.getRangeEnd(contentLength);
			
//			byte[] arr;
			
//			if (rangeEnd == contentLength - 1) {
//				resource.getInputStream().skip(rangeStart);
//				arr = resource.getContentAsByteArray();
//			}
//			else {
//				arr = new byte[(rangeEnd - rangeStart)];
//				
//				resource.getInputStream().read(arr, rangeStart, rangeEnd - rangeStart);
//				
//			}
			
			resource.getInputStream().skip(rangeStart);
			
			res = concatTwoArrays(res, resource.getInputStream().readNBytes(rangeEnd - rangeStart));

		}
		return res;
	}
	
	private byte[] concatTwoArrays(byte[] arr1, byte[] arr2) {
		byte[] newArr = new byte[arr1.length + arr2.length];
		
		System.arraycopy(arr1, 0, newArr, 0, arr1.length);
		System.arraycopy(arr2, 0, newArr, arr1.length, arr2.length);
		
		return newArr;
	}
	
}
