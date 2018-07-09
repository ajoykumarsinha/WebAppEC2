/**
 * 
 */
package controller;

/**
 * @author ajoy
 *
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles requests for the application file upload requests
 */
@Controller
public class FileUploadController {

	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

	UploadedFile ufile;

	public FileUploadController() {
		System.out.println("init RestController");
		ufile = new UploadedFile();
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String getHome(Model model) {
		model.addAttribute("home", "Hello world");

		return "index";
	}

	@RequestMapping(value = "/imageupload", method = RequestMethod.GET)
	public String getImageUpload(Model model) {
		model.addAttribute("imageupload", "Image Upload Page !!");

		return "imageupload";
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public @ResponseBody String upload(@RequestParam("userPhoto") MultipartFile uploadfile) {

		System.out.println(" file.getOriginalFilename() >> " + uploadfile.getOriginalFilename());

		try {

			// Save file on system
			if (!uploadfile.getOriginalFilename().isEmpty()) {
				BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(new File("C:\\Training\\FileUploadTest", uploadfile.getOriginalFilename())));
				outputStream.write(uploadfile.getBytes());
				outputStream.flush();
				outputStream.close();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
 
		return "File has been uploaded sussessfully under C:\\Training\\FileUploadTest";

	}

	/**
	 * Upload multiple file using Spring Controller
	 */
	@RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.POST)
	public @ResponseBody String uploadMultipleFileHandler(@RequestParam("name") String[] names,
			@RequestParam("file") MultipartFile[] files) {

		if (files.length != names.length)
			return "Mandatory information missing";

		String message = "";
		for (int i = 0; i < files.length; i++) {
			MultipartFile file = files[i];
			String name = names[i];
			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				String rootPath = System.getProperty("catalina.home");
				File dir = new File(rootPath + File.separator + "tmpFiles");
				if (!dir.exists())
					dir.mkdirs();

				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

				logger.info("Server File Location=" + serverFile.getAbsolutePath());

				message = message + "You successfully uploaded file=" + name + "<br />";
			} catch (Exception e) {
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		}
		return message;
	}
}