package com.app.audioservice.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;

import com.app.audioservice.utils.AudioFragment;


@Service
public class AudioService {

	public static final int MAX_CHUNK_OF_AUDIO = 1024 * 1024;
	
	private static final String AUDIO_PATH = "classpath:/audio/%s.mp3";

	@Autowired
	private ResourceLoader resourceLoader;

	public AudioFragment getResource(String filename, String rangeHeader) throws IOException {
		Resource resource = resourceLoader.getResource(AUDIO_PATH.formatted(filename));

		List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
		long contentLength = resource.contentLength();

		byte[] res = new byte[] {};

		HttpRange httpRange = ranges.get(0);

		long rangeStart =  httpRange.getRangeStart(contentLength);
		long rangeEnd =  rangeStart + MAX_CHUNK_OF_AUDIO;

		try (InputStream input = resource.getInputStream();) {
			input.skipNBytes(rangeStart);
			res = concatTwoArrays(res, input.readNBytes(MAX_CHUNK_OF_AUDIO));
		}

		return new AudioFragment(res, createRangeHeaderValue(rangeStart, rangeEnd, contentLength));

	}

	private byte[] concatTwoArrays(byte[] arr1, byte[] arr2) {
		byte[] newArr = new byte[arr1.length + arr2.length];

		System.arraycopy(arr1, 0, newArr, 0, arr1.length);
		System.arraycopy(arr2, 0, newArr, arr1.length, arr2.length);

		return newArr;
	}

	private String createRangeHeaderValue(long startRange, long endRange, long contentLength) {
		return "bytes %d-%d/%d".formatted(startRange, endRange, contentLength);
	}
}
