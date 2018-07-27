package deprecated;

public class Baxter {

//	private static FileSpecRepo fsr ;
//	
//	public Baxter(String username) {
//		System.out.println("baxt");
//	}
//
//	public void removeVideo(String filename) {
//		fsr.removeFileSpec(filename);
//	}
//
//	public String renderVids() {
//		return Concatenate.concatenate(fsr.getSpecs(), fsr.getSoundDirectory(), fsr.getOutputDirectory());
//	}
//	// Getters and Setters
//	// *******************************************
//
//	public ArrayList<FileSpec> getSpecs() {
//		return fsr.getSpecs();
//	}
//
//	public int setFileTrim(String filename, String trimStart, String trimEnd) {
//		if (Long.valueOf(trimStart) >= Long.valueOf(trimEnd)) { 
//			return 1 ;
//		}
//		FileSpec tempFS = fsr.getFileSpecByFileName(filename);
//		System.out.println(tempFS.getDuration());
//		if (Long.valueOf(trimStart) * 1000 >= tempFS.getDuration()) {
//			return 2 ;
//		}
//		fsr.getFileSpecByFileName(filename).setTrimStart(trimStart);
//		fsr.getFileSpecByFileName(filename).setTrimEnd(trimEnd);
//		return 0 ;
//	}
//
}
