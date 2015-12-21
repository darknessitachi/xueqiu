import javax.swing.filechooser.FileSystemView;






public class Test {

	public static void main(String[] args) {
		
	        FileSystemView fsv = FileSystemView.getFileSystemView();

	        System.out.println( fsv.getHomeDirectory().getAbsolutePath());
	        
		
	}
	

}
