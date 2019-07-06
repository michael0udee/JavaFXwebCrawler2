package controller.tab;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;

import javafx.stage.DirectoryChooser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import controller.MainController;

public class Tab1Controller {

	private MainController main;
	
	@FXML private Label lbl1 = new Label();
	@FXML private TextField txt1;
	@FXML private Button btn1Search;
	@FXML private TextArea txtArea;
	private String newText;
	private int media_size;
	@FXML private TextField txt2 = new TextField();
	@FXML private Button btn1browse;
	
	@FXML private Button btn1save;
	@FXML private Label lbl2 = new Label();

    private LinkedList<String> list = new LinkedList<>();

	@FXML private void btn1SearchClicked(ActionEvent event)throws IOException{
		System.out.println("Btn 1 search clicked");
		//txtArea.clear();
		newText = "    ";
//		txtArea.setText(txt1.getText());
//		lbl1.setText(txt1.getText());
		
		String url = "http://" + txt1.getText();
		print("Fetching %s...", url);

        Document doc = Jsoup.connect(url).timeout(5*1000).get();
        Elements media = doc.select("[src]");

        int notNeeded = 0;
        for (Element src : media)
        {
        	if(src.tagName().equals("script") || src.attr("abs:src").isEmpty())
        	    notNeeded++;
        }
        media_size = media.size()-notNeeded;
        print("\nMedia: (%d)", media_size);

        for (Element src : media)
        {
        	System.out.println(src.attr("abs:src"));
            if (src.tagName().equals("img") || src.tagName().equals("a"))
            {
                print(" * %s: <%s> %sx%s (%s)",
                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                        trim(src.attr("alt"), 50));
                list.addFirst(src.attr("src"));
            }
            else System.out.println(src.tagName());
//                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
        }
		
	}
	
	private void print(String msg, Object... args) {
		newText = newText + "\n" + String.format(msg, args);
		txtArea.setText(newText);
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
   
    @FXML private void btn1browseClicked(ActionEvent event){
    	
    	System.out.println("Btn 1 browse clicked");
    	
    		Node node = (Node) event.getSource();
            DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory = 
                    directoryChooser.showDialog(node.getScene().getWindow());
			
            if(selectedDirectory == null){
                txt2.setText("No Directory selected");
                btn1save.setDisable(true);
            }else{
                txt2.setText(selectedDirectory.getAbsolutePath());
                btn1save.setDisable(false);
            }
        
    }
    
    @FXML private void btn1saveClicked(ActionEvent event) {
    	System.out.println("Btn 1 save clicked");
    	String theDir = txt2.getText()+"/"+txt1.getText();
    	File f = new File(theDir);
        System.out.println("Directory made: " + f.mkdirs());

    	for(int i=0; i<media_size; i++){
    		try{
    		    //imgFormat = list.get(i).substring(list.get(i).length()-3);
                String imageName = list.get(i).substring(list.get(i).lastIndexOf("/") + 1 );

				System.out.println(imageName);

				URL urlImage = new URL(list.get(i));
                InputStream in = urlImage.openStream();
                OutputStream out = new BufferedOutputStream(new FileOutputStream( theDir + "/" + imageName));
                for (int b; (b = in.read()) != -1;) {
                    out.write(b);
                }
                out.close();
                in.close();
    		//System.out.println(list.get(i));
    		}
    		catch(Exception e){
				System.out.println(e.toString());
    		}
    	}
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Save images");
        alert.setHeaderText("Done!");
        alert.setWidth(150);
        alert.setHeight(100);
        alert.showAndWait();
    }
	
	public void init(MainController mainController) {
		main = mainController;
	}

}
