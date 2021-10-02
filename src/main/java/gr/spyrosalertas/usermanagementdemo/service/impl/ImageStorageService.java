package gr.spyrosalertas.usermanagementdemo.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.List;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import gr.spyrosalertas.usermanagementdemo.exception.FileFormatNotSupportedException;
import gr.spyrosalertas.usermanagementdemo.exception.NoFileException;
import gr.spyrosalertas.usermanagementdemo.exception.ProfileImageNotFoundException;
import gr.spyrosalertas.usermanagementdemo.exception.UnknownErrorException;

// Package private helper class to manipulate user profile images
@Service
class ImageStorageService {

	String dirPath = System.getProperty("user.dir") + File.separator + "profile-images";

	@Value("${file.supported-img-extensions}")
	private List<String> supportedFileExtensions;

	public Resource downloadUserProfileImage(String filename) {

		// Check if user has a profile image
		for (String ext : supportedFileExtensions) {
			String path = dirPath + File.separator + filename + "." + ext;
			File file = new File(path);
			try {
				Resource resource = new UrlResource(file.toURI());
				if (resource.exists() || resource.isReadable()) {
					// If profile image was found, return it as a Resource Object
					return resource;
				}
			} catch (MalformedURLException e) {
				throw new UnknownErrorException();
			}
		}

		// If there was no profile image found, return null
		return null;

	}

	// Upload/Update users profile image (as filename we use the users username)
	public void uploadUserProfileImage(MultipartFile file, String filename) {

		if (file == null || file.getSize() == 0) {
			// If user sent no file
			throw new NoFileException();
		}
		if (!supportedFileExtension(file)) {
			// If file format is not supported throw relative Exception
			throw new FileFormatNotSupportedException("Supported file formats are: " + this.supportedFileExtensions
					.toString().substring(1, this.supportedFileExtensions.toString().length() - 1));
		}

		// Delete old profile image if there is one
		supportedFileExtensions.forEach(ext -> {
			File filepath = new File(dirPath + File.separator + filename + "." + ext);
			try {
				Files.deleteIfExists(filepath.toPath());
			} catch (IOException e) {
				throw new UnknownErrorException();
			}
		});

		// And then save the new profile image
		Tika tika = new Tika();
		try {
			String path = dirPath + File.separator + filename + "." + tika.detect(file.getInputStream()).split("/")[1];
			file.transferTo(new File(path));
		} catch (IOException e) {
			throw new UnknownErrorException();
		}

	}

	public void deleteUserProfileImage(String filename) {

		// Delete profile image if there is one

		boolean isProfileImageDeleted = false;

		for (String ext : supportedFileExtensions) {
			File filepath = new File(dirPath + File.separator + filename + "." + ext);
			try {
				if (Files.deleteIfExists(filepath.toPath())) {
					// If profile image was deleted
					isProfileImageDeleted = true;
					break;
				}
			} catch (IOException e) {
				throw new UnknownErrorException();
			}
		}

		if (!isProfileImageDeleted) {
			// If no profile image was deleted throw error
			throw new ProfileImageNotFoundException();
		}

	}

	public void deleteUserProfileImageIfExists(String filename) {

		for (String ext : supportedFileExtensions) {
			File filepath = new File(dirPath + File.separator + filename + "." + ext);
			try {
				if (Files.deleteIfExists(filepath.toPath())) {
					// If profile image was deleted - stop, we are done
					break;
				}
			} catch (IOException e) {
				throw new UnknownErrorException();
			}
		}

	}

	// -- Private Helper Methods --

	// This method runs when we start the application and creates the profile-images
	// folder if it doesn't exist already
	@EventListener(ApplicationReadyEvent.class)
	private void initializeImageStorage() {
		File directory = new File(dirPath);
		if (!directory.exists()) {
			// If the folder doesn't exist, create it
			directory.mkdir();
		}
	}

	// Check files mime type to see if it's supported or no
	private boolean supportedFileExtension(MultipartFile file) {
		// Get mime type using the tika library
		Tika tika = new Tika();
		boolean isSupported = false;
		try {
			String mimeType = tika.detect(file.getInputStream());
			if (supportedFileExtensions.contains(mimeType.split("/")[1]))
				isSupported = true;
		} catch (IOException e) {
			throw new UnknownErrorException();
		}
		return isSupported;
	}

}
