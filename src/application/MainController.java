package application;



import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.net.URL;
import java.util.ResourceBundle;

import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_highgui.CvCapture;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.helper.opencv_core.CvArr;
import org.bytedeco.javacpp.opencv_imgproc.CvMoments;

import application.ccmFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainController implements Initializable{
	
	@FXML
	private Button btn_start;
	@FXML
	private Button btn_stop;
	@FXML
	private ImageView original;
	@FXML
	private ImageView filtered;
	@FXML
	private ComboBox<String> resolution;
	ObservableList<String> list=FXCollections.observableArrayList("1280 x 720","1920 x 1080","2560 x 1440","3840 x 2160","7680 x 4320");
	public static int w,h;
	


	
	public void startCamera(ActionEvent event) throws AWTException
	{	
		IplImage img1,imgbinG, imgbinB;
		IplImage imghsv;
		Image image=null;
	
		
		CvScalar Bminc = opencv_core.cvScalar(95,150,75,0), Bmaxc = opencv_core.cvScalar(145,255,255,0);
		CvScalar Gminc = opencv_core.cvScalar(40,50,60,0), Gmaxc = opencv_core.cvScalar(80,255,255,0);
		
		CvArr mask;
		
		int w=320,h=240;
		
		imghsv = opencv_core.cvCreateImage(opencv_core.cvSize(w,h),8,3);
		imgbinG = opencv_core.cvCreateImage(opencv_core.cvSize(w,h),8,1);
		imgbinB = opencv_core.cvCreateImage(opencv_core.cvSize(w,h),8,1);
		
		IplImage imgC = opencv_core.cvCreateImage(opencv_core.cvSize(w,h),8,1);
		
		opencv_core.CvSeq contour1 = new opencv_core.CvSeq(), contour2=null;
		opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();
		opencv_imgproc.CvMoments moments = new opencv_imgproc.CvMoments(Loader.sizeof(CvMoments.class));
	
		
		CvCapture capture1 = opencv_highgui.cvCreateCameraCapture(opencv_highgui.CV_CAP_ANY);
		
		opencv_highgui.cvSetCaptureProperty(capture1,opencv_highgui.CV_CAP_PROP_FRAME_WIDTH,w);
		opencv_highgui.cvSetCaptureProperty(capture1,opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT,h);
		
		//int i=1;
				while(true)
				{
						
					img1 = opencv_highgui.cvQueryFrame(capture1);
					//IplImage img1 = IplImage.create(img2.height(), img2.width(), img2.depth(), img2.nChannels());
					
					opencv_core.cvFlip(img1, img1, 1 );
					if(img1 == null){
						System.err.println("No Image");
						break;
						}
					
					//only detect blue color 1,0
					imgbinB = ccmFilter.Filter(img1,imghsv,imgbinB,Bmaxc, Bminc, contour1, contour2, storage,moments,1,0);
					//only detect blue color 0,1
					imgbinG = ccmFilter.Filter(img1,imghsv,imgbinG,Gmaxc, Gminc, contour1, contour2, storage,moments,0,1);
					
					//imgC will store both imgbinB and imgbinG
					opencv_core.cvOr(imgbinB,imgbinG,imgC,mask=null);
					
					
					opencv_highgui.cvShowImage("Combined",imgC);	
					opencv_highgui.cvShowImage("Original",img1);
					//original.setStyle("-fx-background-image: url('img1');");
					//original.setImage(img1);		
					char c = (char)opencv_highgui.cvWaitKey(15);
					if(c=='q') break;
						
					}
	}



	public void stopCamera() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		resolution.setItems(list);
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		w = gd.getDisplayMode().getWidth();
		h = gd.getDisplayMode().getHeight();
		switch(h){
		//"1280 x 720","1920 x 1080","2560 x 1440","3840 x 2160","7680 x 4320"
		case 720:
			resolution.getSelectionModel().select(0);
			w=1280;
			h=720;
			break;
		case 1080:
			resolution.getSelectionModel().select(1);
			w=1920;
			h=1080;
			break;
		case 1440:
			resolution.getSelectionModel().select(2);
			w=2560;
			h=1440;
			break;
		case 2160:
			resolution.getSelectionModel().select(3);
			w=3840;
			h=2160;
			break;
		case 4320:
			resolution.getSelectionModel().select(4);
			w=7680;
			h=4320;
			break;	
		default: 
			break;
		}
		System.out.println("wid"+w);
		System.out.println("hei"+h);
		
	}
	public void resolutionChanged(ActionEvent event) {
		String tempresolution=resolution.getValue();
		
		switch(tempresolution){
		case "1280 x 720":
			w=1280;
			h=720;
			break;
		case "1920 x 1080":
			w=1920;
			h=1080;
			break;
		case "2560 x 1440":
			w=2560;
			h=1440;
			break;
		case "3840 x 2160":
			w=3840;
			h=2160;
			break;
		case "7680 x 4320":
			w=7680;
			h=4320;
			break;	
		default: 
			break;
		}
		System.out.println(w);
		System.out.println(h);
		
	}
	
}
