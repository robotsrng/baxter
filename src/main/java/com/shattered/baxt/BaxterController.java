package com.shattered.baxt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

@SessionAttributes("fsr")
@Controller("/baxt")
public class BaxterController {

	@ModelAttribute("fsr")
	public FileSpecRepo fsr() {
		return new FileSpecRepo();
	}
	
	
	public BaxterController() {
		System.out.println("baxtC");
	}
	
	private String currentUserName(Principal principal) { 
		return principal.getName();
	}

	@GetMapping("/baxt/baxt-start")
	public String RefreshBaxterStart(Model model, Principal principal, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
		model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
		model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
		model.addAttribute("specFiles", fsr.getSpecs());
		return "baxter-start" ;
	}
	
	@RequestMapping("/baxt/upload-page")
	public String showUploadVideoPage(Model model, Principal principal, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		model.addAttribute("storage", UploadController.getUserStorage(currentUserName(principal)));
		model.addAttribute("username", currentUserName(principal));
		model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
		model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
		return "upload-page" ;
	}
	
	@RequestMapping("/baxt/show-completed-videos")
	public String showCopmletedVideoPage(Model model, Principal principal, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		model.addAttribute("storage", UploadController.getUserStorage(currentUserName(principal)));
		model.addAttribute("username", currentUserName(principal));
		model.addAttribute("completedFiles", UploadController.getUserCompletedFiles(currentUserName(principal)));
		return "completed-videos-page" ;
	}
	
	@PostMapping("baxt/upload-sound")
	public String refreshUploadSoundPage(Model model , @RequestParam("sound") MultipartFile multiPartFile, Principal principal, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		if(UploadController.uploadSound(multiPartFile, currentUserName(principal))) {
			model.addAttribute("message", currentUserName(principal));
			model.addAttribute("storage", UploadController.getUserStorage(currentUserName(principal)));
			model.addAttribute("username", currentUserName(principal));
			model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
			model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
			return "upload-page" ;
		}else {
			model.addAttribute("stupidmessage", currentUserName(principal) + ", You need to use a .wav or .mp3 file, stupid.");
			model.addAttribute("storage", UploadController.getUserStorage(currentUserName(principal)));
			model.addAttribute("username", currentUserName(principal));
			model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
			model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
			return "upload-page";
		}
	}

	@PostMapping("baxt/upload-video")
	public String refreshUploadPage(Model model , @RequestParam("videos") MultipartFile[] multiPartFiles, Principal principal, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		int errors = UploadController.uploadVideos(multiPartFiles, currentUserName(principal));
		if(errors == -1) { 
			model.addAttribute("stupidmessage", "You have used all of your storage space") ;
			model.addAttribute("storage", UploadController.getUserStorage(currentUserName(principal)));
			model.addAttribute("username", currentUserName(principal));
			model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
			model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
		}
		if(errors > 0) { 
			model.addAttribute("message", errors + " of your files were not of the correct format") ;
			model.addAttribute("storage", UploadController.getUserStorage(currentUserName(principal)));
			model.addAttribute("username", currentUserName(principal));
			model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
			model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
		}
		model.addAttribute("username", currentUserName(principal));
		model.addAttribute("storage", UploadController.getUserStorage(currentUserName(principal)));
		model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
		model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
		return "upload-page" ;
	}

	@RequestMapping("baxt/remove-file")
	public String removeFile(Model model, Principal principal, @RequestParam("filepath") String filepath, @RequestParam("username") String username, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		UploadController.removeFile(filepath, username);
		model.addAttribute("storage", UploadController.getUserStorage(currentUserName(principal)));
		model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
		model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
		return "redirect:/baxt/upload-page";
	}
	
	@RequestMapping("baxt/remove-completed-file")
	public String removeCompletedFile(Model model, Principal principal, @RequestParam("filepath") String filepath, @RequestParam("username") String username, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		UploadController.removeFile(filepath, username);
		model.addAttribute("storage", UploadController.getUserStorage(currentUserName(principal)));
		model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
		model.addAttribute("completedFiles", UploadController.getUserCompletedFiles(currentUserName(principal)));
		return "redirect:/baxt/show-completed-videos";
	}
	
	@RequestMapping("baxt/rename-file")
	public String renameFile(Model model, Principal principal, @RequestParam("filepath") String filepath, @RequestParam("username") String username, @RequestParam("newName") String newName, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		UploadController.renameFile(filepath, username, newName);
		model.addAttribute("storage", UploadController.getUserStorage(currentUserName(principal)));
		model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
		model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
		return "redirect:/baxt/upload-page";
	}
	
	@PostMapping("baxt/remove-file-spec")
	public String removeFileSpec(Model model, Principal principal, @RequestParam("filepath") String filename, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		fsr.removeFileSpec(filename);
		model.addAttribute("fsr", fsr);
		model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
		model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
		model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
		model.addAttribute("specFiles", fsr.getSpecs());
		return "baxter-start";
	}
	
	@RequestMapping("baxt/empty-fsr")
	public String emptyFileSpecRepo(Model model, Principal principal, @ModelAttribute("fsr") FileSpecRepo fsr) {
		fsr.emptyRepo();
		model.addAttribute("fsr", fsr);
		model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
		model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
		model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
		model.addAttribute("specFiles", fsr.getSpecs());
		return "redirect:/baxt/baxt-start";
	}
	
	@RequestMapping("baxt/select-sound")
	public String selectSound(Model model, Principal principal, @RequestParam("filename") String filename, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		fsr.setSoundDirectory(UploadController.getUserSoundDir(filename, currentUserName(principal)));
		model.addAttribute("fsr", fsr);
		model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
		model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
		model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
		model.addAttribute("specFiles", fsr.getSpecs());
		return "redirect:/baxt/baxt-start";
	}

	@RequestMapping("baxt/select-video")
	public String selectVideo(Model model, Principal principal, @RequestParam("filepath") String filepath, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		fsr.addFile(filepath);
		model.addAttribute("fsr", fsr);
		model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
		model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
		model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
		model.addAttribute("specFiles", fsr.getSpecs());
		return "redirect:/baxt/baxt-start";
	}
	@RequestMapping("baxt/select-video-all")
	public String selectVideoAll(Model model, Principal principal, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		fsr.addFileMulti(UploadController.getUserVideoFiles(currentUserName(principal)));
		model.addAttribute("fsr", fsr);
		model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
		model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
		model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
		model.addAttribute("specFiles", fsr.getSpecs());
		return "redirect:/baxt/baxt-start";
	}
	
	@PostMapping("baxt/remove-file-spec-from-trim")
	public String removeFileSpecFromTrim(Model model, Principal principal, @RequestParam("filepath") String filename, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		fsr.removeFileSpec(filename);
		model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
		model.addAttribute("fsr", fsr);
		model.addAttribute("files", fsr.getSpecs());
		return "redirect:/baxt/trim-video";
	}
	
	@RequestMapping("baxt/move-video-up")
	public String moveVideoUp(Model model, Principal principal, @RequestParam("file") String filename, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
			model.addAttribute("message", CodeRepo.getMessage(code)); 
			fsr.moveVideoUp(fsr.getFileSpecByFileName(filename));
			model.addAttribute("fsr", fsr);
			model.addAttribute("files", fsr.getSpecs());
			return "redirect:/baxt/trim-video";
		}
	
	
	@RequestMapping("baxt/move-video-down")
	public String moveVideoDown(Model model, Principal principal, @RequestParam("file") String filename, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code)); 
		fsr.moveVideoDown(fsr.getFileSpecByFileName(filename));
		model.addAttribute("fsr", fsr);
		model.addAttribute("files", fsr.getSpecs());
		return "redirect:/baxt/trim-video";
	}
	
	@RequestMapping("baxt/trim-video")
	public String trimVideo(Model model, Principal principal, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) throws Exception{
		model.addAttribute("message", CodeRepo.getMessage(code));
		if(fsr.getSoundDirectory().equals("")) { 
			model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
			model.addAttribute("soundFiles", UploadController.getUserSoundFiles(currentUserName(principal)));
			model.addAttribute("videoFiles", UploadController.getUserVideoFilesDisplay(currentUserName(principal)));
			model.addAttribute("specFiles", fsr.getSpecs());
			model.addAttribute("stupidmessage", "You need to pick a sound file!");
			return "baxter-start";
		}
		model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
		PictureWrapper pw = new PictureWrapper(getImage(new File(fsr.getSoundDirectory()).getName(), principal), new File(fsr.getSoundDirectory()).getName() );
		model.addAttribute("pw", pw);
		model.addAttribute("fsr", fsr);
		model.addAttribute("files", fsr.getSpecs());
		return "trim-video";
	}
	
	@RequestMapping("baxt/copy-video")
	public String copyVideo(Model model, Principal principal, @RequestParam("filepath") String filepath, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		fsr.addFile(filepath);
		model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
		model.addAttribute("fsr", fsr);
		model.addAttribute("files", fsr.getSpecs());
		return "redirect:/baxt/trim-video";
	}
	
	@RequestMapping("baxt/sort-filespec-alphabet")
	public String sortFileSpecAlphabet(Model model, Principal principal, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
 		fsr.sortByName();
		model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
		model.addAttribute("fsr", fsr);
		model.addAttribute("files", fsr.getSpecs());
		return "redirect:/baxt/trim-video";
	}

	@RequestMapping("baxt/randomize-trim-all")
	public String randomizeTrim(Model model, Principal principal, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		fsr.randomizeAll() ;
		
		model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
		model.addAttribute("fsr", fsr);
		model.addAttribute("files", fsr.getSpecs());
		return "redirect:/baxt/trim-video";
	}
	@RequestMapping("baxt/set-sound-trim")
	public String setSoundTrim(Model model, Principal principal, @RequestParam("soundTrim") String soundTrim, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code)); 
		int res = fsr.setSoundTrim(soundTrim);
		if ( res > 0)  {
			model.addAttribute("message", "Your trim amounts are out of bounds.");
		}
		model.addAttribute("sound", new File(fsr.getSoundDirectory()).getName());
		model.addAttribute("fsr", fsr);
		model.addAttribute("files", fsr.getSpecs());
		return "redirect:/baxt/trim-video";
	}
	
	@RequestMapping("baxt/set-video-trim")
	public String setVideoTrim(Model model, Principal principal, @RequestParam("filename") String filename, @RequestParam("trimStart") String trimStart, @RequestParam("trimEnd") String trimEnd, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code)); 
		int res = fsr.setFileTrim(filename, trimStart, trimEnd);
		if ( res > 0)  {
			model.addAttribute("message", "Your trim amounts are out of bounds.");
		}
		model.addAttribute("fsr", fsr);
		model.addAttribute("files", fsr.getSpecs());
		return "redirect:/baxt/trim-video";
	}
	
	@RequestMapping("baxt/baxt-video")
	public String baxtVideo(Model model, Principal principal, @ModelAttribute("fsr") FileSpecRepo fsr, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		if(fsr.getSpecs().size() < 1 ) { 
			model.addAttribute("fsr", fsr);
			model.addAttribute("stupidmessage", "You cannot baxt without videos, idiot.");
			return "upload-video";
		}
		fsr.setOutputDirectory(UploadController.getUserOutputDir(currentUserName(principal)));
		long startTime = System.nanoTime();
		String finalVidLoc = Concatenate.concatenate(fsr);
		long endTime = System.nanoTime();
		System.out.println("time taken = " + (endTime - startTime) / 1000000 );
		model.addAttribute("fsr", fsr);
		model.addAttribute("finalVidLoc", finalVidLoc);
		model.addAttribute("filename", new File(finalVidLoc).getName());
		return "download-video";
	}

	@GetMapping("baxt/download")
	public void downloadCompletedVideo(Model model, HttpServletResponse response, @RequestParam("finalVidLoc") String finalVidLoc, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code)); 
		try {
			File download = new File (finalVidLoc) ;
			FileInputStream is = new FileInputStream(download) ;
			response.setContentType("video/mp4");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + download.getName() + "\"");
			IOUtils.copy(is, response.getOutputStream()) ; 
			response.flushBuffer();
		}catch(Exception e) { 
			model.addAttribute("message", CodeRepo.getMessage("bad-download"));
		}
	}
	
	@RequestMapping("/success")
	public String success(Model model, @RequestParam(value="code", required=false) String code) { 
		model.addAttribute("message", CodeRepo.getMessage(code));
		return "success" ;
	}

	@RequestMapping("/show-error")
	public String error(Model model, @RequestParam(value="code", required=false) String code) { 
		model.addAttribute("message", CodeRepo.getMessage(code));
		return "error" ;
	}
	
	public byte[] getImage(String imageName, Principal principal) throws IOException{ 
		return UploadController.getImage(imageName, currentUserName(principal));
	}
	
}
