package controller.tab;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import controller.MainController;

public class Tab2Controller {

	private MainController main;
	
	@FXML public Label chooseWebLabel = new Label();
	@FXML private CheckBox chkBoxGoogle, chkBoxPicSearch, chkBoxLmgr, chkBoxFS, chkBoxDeviant;
	@FXML private CheckBox chkBoxImgur, chkBoxWh, chkBoxWiki;
	@FXML private Button btn2search;
	@FXML public TextField txt3;
	@FXML public TextField txt4;
	@FXML private Button btn2browse;
	@FXML private Button btn2save;
	
	private String keyword, site;
	private int s=0;
	private int n = 150;
	private String wynik1[] = new String[n];
    private String wynik2[] = new String[n];
	//LinkedList list2 = new LinkedList();
	BufferedImage image2 = null;
	Document doc2 = null;
	
	private int GoogleImSelected(String keyw)throws IOException{
		site = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=";
		keyw = keyw.replace(' ', '+');
		site += keyword;
		System.out.println(site);
        doc2 = Jsoup.connect(site).userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) "
   		 		+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 "
   		 		+ "Safari/537.36 OPR/30.0.1835.59").timeout(10*1000).referrer("http://www.google.com").get();
        Element body = doc2.body();
        String strBody = body.text().toString();
        wynik1 = strBody.split("\"");
        for(int i=0;i<wynik1.length;i++){
        	//System.out.println("\n"+wynik1[i]);
        	if(wynik1[i].length() > 4 && wynik1[i].charAt(wynik1[i].length()-4)=='.' && wynik1[i].charAt(0)=='h'){
        		wynik2[s]=wynik1[i];
        		if(s>0 && wynik2[s].equals(wynik2[s-1]))s--;
        		s++;
        	}
        }
        
        for(int i=0;i<wynik2.length;i++){
        	System.out.println("\n"+wynik2[i]);
        }
        System.out.println("S: "+s);
		return s;
	}
	
	private int PicSearchSelected(String keyw)throws IOException{
		keyw = keyw.replace(' ', '+');
		site = "http://www.picsearch.com/index.cgi?q="+keyw;
		System.out.println("site: "+site);
		int i = 0;
		Document doc3;
		Elements media;
		Connection con = Jsoup.connect(site).followRedirects(true).timeout(10000);
		con.ignoreHttpErrors(true);
		Connection.Response resp = con.execute();
		System.out.println("Status code = " + resp.statusCode());   
        System.out.println("Status msg  = " + resp.statusMessage());
         if(resp.statusCode() == 200) {
             doc2 = con.get();
             Elements links = doc2.select("a[href]");
             print("\nLinks: (%d)", links.size());
             for (Element link : links) {
            	 print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
            	 if(link.attr("abs:href").endsWith("unsafe"))
               	 {
            		 wynik1[i] = link.attr("abs:href");
    	       		 System.out.println("o: "+wynik1[i]);
    	       		doc3 = Jsoup.connect(wynik1[i]).get();
   	       		 	media = doc3.select("a[href]");
   	       		 	for (Element src : media)
   	       		 	{
   	       		 		print(" * a: <%s>  (%s)", src.attr("abs:href"), trim(src.text(), 35));
   	       		 		if(src.text().equals("Full-size image"))
   	       		 		{
   	       		 		wynik2[s]=src.attr("abs:href");
   	       		 		s++;
   	       		 		}
   	       		 	}
   	       		 	i++;
               	 }
             }
         } else System.out.println("ERROR!!!");
         for(int j=0;j<wynik2.length;j++){
         	System.out.println("\nZdj: "+wynik2[j]);
         }
        System.out.println("S: "+s);
		return s;
	}
	
	private int LomographicSelected(String keyw)throws IOException{
		site = "http://www.lomography.com/search/photos?query=";
		keyw = keyw.replace(' ', '+');
		site += keyw;
		int i=0;
		Document doc3;
		Elements media;
		System.out.println("site: "+site);
		doc2 = Jsoup.connect(site).followRedirects(true).timeout(10000).get();
		Elements links = doc2.select("a[href]");
		print("\nLinks: (%d)", links.size());
        for (Element link : links) {
       	 print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
       	 if(link.attr("abs:href").startsWith("http://www.lomography.com/search/photos/"))
       	 {
       		 wynik1[i]=link.attr("abs:href");
       		 //System.out.println("o: "+wynik1[i]);
       		 doc3 = Jsoup.connect(wynik1[i]).userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) "
       		 		+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 "
       		 		+ "Safari/537.36 OPR/30.0.1835.59")
       		 		.referrer("http://www.google.com").followRedirects(true).get();
       		 media = doc3.select("[src]");
       		for (Element src : media)
       		{
              	 print(" * src: <%s>  (%s)", src.attr("abs:src"), trim(src.text(), 35));
              	 if(src.attr("abs:src").startsWith("http://cdn") && !(src.attr("abs:src").endsWith(".js")))
              	 {
              		 wynik2[s]=src.attr("abs:src").substring(0, src.attr("abs:src").length()-46);
              	 	 s++;
              	 }
       		}
       		 i++;
       	 }
        }
        for(int j=0;j<wynik2.length;j++){
        	System.out.println("\n"+wynik2[j]);
        }
        System.out.println("S3: "+s);
		return s;
	}
	
	private int PhotoshelterSelected(String keyw)throws IOException{
		site = "http://www.photoshelter.com/search?_ACT=search&I_DSC_AND=t&I_DSC=";
		keyw = keyw.replace(' ', '+');
		site += keyw;
		System.out.println(site);
		int i=0;
		Document doc3;
		Elements media;
		doc2 = Jsoup.connect(site).followRedirects(true).timeout(10000).get();
		Elements links = doc2.select("a[href]");
		print("\nLinks: (%d)", links.size());
		for (Element link : links) {
	       	 print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
	       	 if(link.attr("abs:href").startsWith("http://www.photoshelter.com/image/"))
	       	 {
	       		 wynik1[i] = link.attr("abs:href");
	       		 System.out.println("o: "+wynik1[i]);
	       		 doc3 = Jsoup.connect(wynik1[i]).get();
	       		 media = doc3.select("[src]");
	       		 for (Element src : media)
	       		 {
	       			 print(" * src: <%s>  (%s)", src.attr("abs:src"), trim(src.text(), 35));
	       			 if(src.attr("abs:src").startsWith("http://cdn") && !(src.attr("abs:src").endsWith(".js")) && !(src.attr("abs:src").endsWith(".png")))
	       			 {
	       				//System.out.println("z: "+src.attr("abs:src")); 
	              		wynik2[s] = src.attr("abs:src");
	              	 	s++;
	              	 }
	       		 }
	       		 i++;
	       	 }
		}
		return s;
	}
	
	private int DeviantArtSelected(String keyw)throws IOException{
		site = "http://www.deviantart.com/browse/all/?section=&global=1&q=";
		keyw = keyw.replace(' ', '+');
		site += keyw;
		System.out.println(site);
		int i=0, w=0;
		Document doc3;
		Elements media;
		doc2 = Jsoup.connect(site).followRedirects(true).timeout(10000).get();
		Elements links = doc2.select("a[href]");
		print("\nLinks: (%d)", links.size());
		for (Element link : links) {
	       	 print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
	       	 if(link.attr("abs:href").contains("deviantart.com/art/"))
	       	 {
	       		 wynik1[i] = link.attr("abs:href");
	       		 System.out.println("o: "+wynik1[i]);
	       		 doc3 = Jsoup.connect(wynik1[i]).timeout(60*1000).get();
	       		 media = doc3.select("[src]");
	       		 for (Element src : media)
	       		 {
	       			 print(" * src: <%s>  (%s)", src.attr("abs:src"), trim(src.text(), 35));
	       			 if(!src.attr("width").equals("")) w = Integer.parseInt(src.attr("width"));
	       			 else w = 0;
	       			 if(w > 300 && !(src.attr("abs:src").endsWith(".js")))
	       			 {
	       				System.out.println("z: "+src.attr("abs:src")); 
	       				System.out.println("width: "+src.attr("width"));
	       				if(s>=n)break;
	              		wynik2[s] = src.attr("abs:src");
	              		if(s>0 && wynik2[s].equals(wynik2[s-1]))s--;
	              	 	s++;
	              	 }
	       		 }
	       		 i++;
	       	 }
	       	if(i>=n)break;
		}
		for(int j=0;j<wynik2.length;j++){
        	System.out.println("\n"+wynik2[j]);
        }
		return s;
	}
	
	private int ImgurSelected(String keyw)throws IOException{
		site = "http://imgur.com/search/score/all?q_type=jpg&q_all=";
		keyw = keyw.replace(' ', '+');
		site += keyw;
		System.out.println(site);
		int i = 0;
		Document doc3;
		Elements media;
		doc2 = Jsoup.connect(site).followRedirects(true).timeout(10000).get();
		Elements links = doc2.select("a[href]");
		print("\nLinks: (%d)", links.size());
		for (Element link : links) 
		{
	       	 print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
	       	 if(link.attr("abs:href").startsWith("http://imgur.com/gallery/"))
	       	 {
	       		wynik1[i] = link.attr("abs:href");
	       		System.out.println("o: "+wynik1[i]);
	       		doc3 = Jsoup.connect(wynik1[i]).timeout(60*1000).get();
	       		media = doc3.select("[src]");
	       		for (Element src : media)
	       		 {
	       			 print(" * src: <%s>  (%s)", src.attr("abs:src"), trim(src.text(), 35));
	       			 if(src.attr("abs:src").startsWith("http://i.imgur.com/"))
	       			 {
	       				System.out.println("org: "+src.attr("abs:src")); 
	       				if(s>=n)break;
	              		wynik2[s] = src.attr("abs:src");
	              	 	s++;
	       			 }
	       		 }
	       		i++;
	       	 }
		}
		for(int j=0;j<wynik2.length;j++){
        	System.out.println("\nZdj: "+wynik2[j]);
        }
		return s;
	}
	
	private int WallHavenSelected(String keyw)throws IOException{
		keyw = keyw.replace(' ', '+');
		site = "http://alpha.wallhaven.cc/search?q="+keyw+"&categories=111&purity=100&sorting=relevance&order=desc";
		System.out.println(site);
		int i = 0;
		Document doc3;
		Elements media;
		doc2 = Jsoup.connect(site).followRedirects(true).timeout(10000).get();
		Elements links = doc2.select("a[href]");
		print("\nLinks: (%d)", links.size());
		for (Element link : links) 
		{
	       	 print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
	       	 if(link.attr("abs:href").startsWith("http://alpha.wallhaven.cc/wallpaper/") && link.attr("abs:href").length() < 45)
	       	 {
	       		wynik1[i] = link.attr("abs:href");
	       		System.out.println("o: "+wynik1[i]);
	       		doc3 = Jsoup.connect(wynik1[i]).timeout(60*1000).get();
	       		media = doc3.select("[src]");
	       		for (Element src : media)
	       		 {
	       			 print(" * src: <%s>  (%s)", src.attr("abs:src"), trim(src.text(), 35));
	       			 if(src.attr("abs:src").endsWith(".jpg"))
	       			 {
	       				System.out.println("org: "+src.attr("abs:src")); 
	       				if(s>=100)break;
	              		wynik2[s] = src.attr("abs:src");
	              	 	s++;
	       			 }
	       		 }
	       		i++;
	       	 }
		}
		for(int j=0;j<wynik2.length;j++){
        	System.out.println("\nZdj: "+wynik2[j]);
        }
		return s;
	}
	
	private int WikiSelected(String keyw)throws IOException{
		keyw = keyw.replace(' ', '_');
		site = "http://pl.wikipedia.org/wiki/"+keyw;
		System.out.println(site);
		int i = 0, il = 0;
		Document doc3;
		Elements media;
		doc2 = Jsoup.connect(site).followRedirects(true).userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) "
   		 		+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 "
   		 		+ "Safari/537.36 OPR/30.0.1835.59")
   		 		.referrer("http://www.google.com").followRedirects(true).timeout(10*1000).get();
		Elements links = doc2.select("a[href]");
		print("\nLinks: (%d)", links.size());
		for (Element link : links) 
		{
			//print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
			if(link.attr("abs:href").endsWith(".jpg") || link.attr("abs:href").endsWith(".png") || link.attr("abs:href").endsWith(".svg"))
			{
				wynik1[i] = link.attr("abs:href");
	       		//System.out.println("o: "+wynik1[i]);
	       		doc3 = Jsoup.connect(wynik1[i]).timeout(60*1000).get();
	       		media = doc3.select("[src]");
	       		for (Element src : media)
	       		 {
	       			 //print(" * src: <%s>  (%s)", src.attr("abs:src"), trim(src.text(), 35));
	       			 if(il == 1)
	       			 {
	       				System.out.println("org: "+src.attr("abs:src")); 
	       				//if(s>=50)break;
	              		wynik2[s] = src.attr("abs:src");
	              		if(s>0 && wynik2[s].equals(wynik2[s-1]))s--;
	              	 	s++;
	       			 }
	       			 il++;
	       		 }
	       		i++;
	       		il = 0;
			}

		}
		for(int j=0;j<wynik2.length;j++){
        	System.out.println("\nZdj: "+wynik2[j]);
        }
		return s;
	}
	
	@FXML private void btn2searchClicked(ActionEvent event) throws IOException{
		System.out.println("Btn 2 search clicked");
		wynik1 = null;
        wynik1 = new String[200];
        wynik2 = null;
        wynik2 = new String[200];
		s = 0;
	
		keyword = txt3.getText();
		print("Searching images for '%s'...", keyword);
		
		if(chkBoxGoogle.isSelected()){
			System.out.println("CheckBox Google Images selected");
			GoogleImSelected(keyword);
			//site = "https://www.google.com/search?site=imghp&tbm=isch&source=hp&biw=1366&bih=631&q=";
		}
		System.out.println("S2: "+s);
		if(chkBoxPicSearch.isSelected()){
			System.out.println("CheckBox PicSearch selected");
			PicSearchSelected(keyword);
		}
		
		if(chkBoxLmgr.isSelected()){
			System.out.println("CheckBox Lomographic selected");
			LomographicSelected(keyword);
		}
    
		if(chkBoxFS.isSelected()){
			System.out.println("CheckBox Photoshelter selected");
			PhotoshelterSelected(keyword);
		}
		
		if(chkBoxDeviant.isSelected()){
			System.out.println("CheckBox DeviantArt selected");
			DeviantArtSelected(keyword);
		}
		
		if(chkBoxImgur.isSelected()){
			System.out.println("CheckBox Imgur selected");
			ImgurSelected(keyword);
		}
		
		if(chkBoxWh.isSelected()){
			System.out.println("CheckBox WallHaven selected");
			WallHavenSelected(keyword);
		}
		
		if(chkBoxWiki.isSelected()){
			System.out.println("CheckBox Wikipedia selected");
			WikiSelected(keyword);
		}
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Searching images");
		alert.setWidth(150);
        alert.setHeight(100);
        alert.setHeaderText("Result: "+ s +" images");
        alert.showAndWait();
//        Elements media = doc2.select("[src]");
//        
//        Elements imports = doc2.select("link[href]");
//       
//        for (Element src : media){
//        	dotIndex = src.attr("abs:src").length()-4;
////        	if(dotIndex < 0)dotIndex = 0;
////        	System.out.println(dotIndex);
////        	System.out.println(src.attr("abs:src").charAt(dotIndex));
////        	System.out.println(src.attr("abs:src"));
//        	if(src.tagName().equals("script") || src.attr("abs:src").equals("") || src.attr("abs:src").charAt(dotIndex) != '.')scr++;
//        }
//        media_size = media.size()-scr;
//        print("\nMedia: (%d)", media_size);
//        for (Element src : media) {
//        	scr = 0;
//        	dotIndex = src.attr("abs:src").length()-4;
//        	if(dotIndex < 0)dotIndex = 0;
//        	System.out.println(src.attr("abs:src"));
//            if (src.tagName().equals("img") && src.attr("abs:src") != "" && src.attr("abs:src").charAt(dotIndex) == '.'){
//
//                print(" * %s: <%s> %sx%s (%s)",
//                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
//                        trim(src.attr("alt"), 50));
//                list2.wstawNaPoczatek(src.attr("src"));
//            }
////            else
////                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
//        }
//        
//        print("\nImports: (%d)", imports.size());
//        for (Element link : imports) {
//            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
//        }
	}
	
	@FXML private void btn2browseClicked(ActionEvent event){
    	
    	System.out.println("Btn 2 browse clicked");
    	
    		Node node = (Node) event.getSource();
            DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory = 
                    directoryChooser.showDialog(node.getScene().getWindow());
			
            if(selectedDirectory == null){
                txt4.setText("No Directory selected");
                btn2save.setDisable(true);
            }else{
                txt4.setText(selectedDirectory.getAbsolutePath());
                btn2save.setDisable(false);
            }
        
    }

	@FXML private void btn2saveClicked(ActionEvent event) {
		System.out.println("Btn 2 save clicked");
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Save images");
		alert.setWidth(150);
        alert.setHeight(100);
        alert.setHeaderText("Saving...");
        alert.show();
		String imgFormat;
		String theDir = txt4.getText()+"\\"+keyword;
		new File(theDir).mkdirs();
		for(int i=0; i<s; i++){
			try {
				imgFormat = wynik2[i].substring(wynik2[i].length()-3);
				//if(chkBoxLmgr.isSelected())imgFormat = wynik2[i].substring(wynik2[i].length()-46,wynik2[i].length()-49);
				System.out.println(wynik2[i]);
    			URL url = new URL(wynik2[i]);
        		image2 = ImageIO.read(url);
    			ImageIO.write(image2, imgFormat, new File(theDir + "\\" + (i+1) + "."+imgFormat));
			}
			catch(Exception e) {
                System.out.println(e.toString());
            }
		}
		alert.close();
        alert.setHeaderText("Done!");
        alert.showAndWait();
        wynik1 = null;
        wynik1 = new String[200];
        wynik2 = null;
        wynik2 = new String[200];
        s = 0;
	}
	
	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
		//System.out.println(newText);
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
	
	public void init(MainController mainController) {
		main = mainController;
	}
}