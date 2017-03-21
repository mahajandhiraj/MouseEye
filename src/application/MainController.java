package application;



import static org.bytedeco.javacpp.opencv_highgui.cvDestroyWindow;
import static org.bytedeco.javacpp.opencv_highgui.cvReleaseCapture;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



public class MainController implements Initializable{
	
	
	
	@FXML
	private Button btn_start;
	@FXML
	private Button btn_stop;
	@FXML
	private Slider cslider1,cslider2,cslider3,cslider1stop,cslider2stop,cslider3stop;
	@FXML
	private Slider lslider1,lslider2,lslider3,lslider1stop,lslider2stop,lslider3stop;
	
	@FXML
	private ImageView original;
	@FXML
	private ImageView filtered;
	@FXML
	private ComboBox<String> resolution;
	ObservableList<String> list=FXCollections.observableArrayList("1280 x 720","1920 x 1080","2560 x 1440","3840 x 2160","7680 x 4320");
	public static int wscreen,hscreen;
	
	private static final double cursor[]={95,145,150,255,75,255};
	private static final double leftclick[]={40,80,50,255,60,255};
	

	
	//ERROR DUE TO PASSING OF WIDTH RATIO AND HEIGHTRATIO
	
	private static boolean flag;
	
	
	public void startCamera(ActionEvent event) throws AWTException
	{	
		flag=true;
		IplImage img1,imgbinG, imgbinB;
		IplImage imghsv;
	
		
		/*CvScalar Bminc = opencv_core.cvScalar(cursor[0],cursor[2],cursor[4],0), Bmaxc = opencv_core.cvScalar(cursor[1],cursor[3],cursor[5],0);
		CvScalar Gminc = opencv_core.cvScalar(leftclick[0],leftclick[2],leftclick[4],0), Gmaxc = opencv_core.cvScalar(leftclick[1],leftclick[3],leftclick[5],0);
		*/
		CvArr mask;
		
		int widthratio=(wscreen/320)+1;
		int heightratio=(hscreen/240)+1;
		int temp;
		temp=wscreen+hscreen;
		
		
		
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
				while(flag)
				{	
					CvScalar Bminc = opencv_core.cvScalar(cursor[0],cursor[2],cursor[4],0), Bmaxc = opencv_core.cvScalar(cursor[1],cursor[3],cursor[5],0);
					CvScalar Gminc = opencv_core.cvScalar(leftclick[0],leftclick[2],leftclick[4],0), Gmaxc = opencv_core.cvScalar(leftclick[1],leftclick[3],leftclick[5],0);

					
					if(temp!=(wscreen+hscreen)){
					widthratio=(wscreen/320)+1;
					heightratio=(hscreen/240)+1;
					temp=wscreen+hscreen;
					System.out.println("\n"+widthratio+"\t"+heightratio);
					}
					
					
						
					img1 = opencv_highgui.cvQueryFrame(capture1);
					//IplImage img1 = IplImage.create(img2.height(), img2.width(), img2.depth(), img2.nChannels());
					
					opencv_core.cvFlip(img1, img1, 1 );
					if(img1 == null){
						System.err.println("No Image");
						break;
						}
					
					//only detect blue color 1,0
					imgbinB = ccmFilter.Filter(img1,imghsv,imgbinB,Bmaxc, Bminc, contour1, contour2, storage,moments,1,0,widthratio,heightratio);
					//only detect blue color 0,1
					imgbinG = ccmFilter.Filter(img1,imghsv,imgbinG,Gmaxc, Gminc, contour1, contour2, storage,moments,0,1,widthratio,heightratio);
					
					//imgC will store both imgbinB and imgbinG
					opencv_core.cvOr(imgbinB,imgbinG,imgC,mask=null);
					
					Image imageToShow = Utils.mat2Image(img1);
					updateImageView(original, imageToShow);
					opencv_highgui.cvShowImage("Combined",imgC);	
					opencv_highgui.cvShowImage("Original",img1);
					
					//cvSaveImage("C:/Users/Dhiraj/Documents/Eclipse-Workspace/MouseEye/_createdimg/Original.jpg",img1);
					//original.setStyle("-fx-background-image: url('/_createdimg/Original.jpg');");
					//original.setImage("\_createdimg\Original.jpg");	
					
					
					char c = (char)opencv_highgui.cvWaitKey(30);
					if(c=='q') break;
						
					}
				if (!flag)
				{
					cvDestroyWindow("Combined");
					cvDestroyWindow("Original");
					cvReleaseCapture(capture1);
				}
	}
	
	
	public void onSet(ActionEvent event) throws AWTException
	{	
		
		flag=true;
		IplImage img1,imgbinG, imgbinB;
		IplImage imghsv;
	
		
		/*CvScalar Bminc = opencv_core.cvScalar(cursor[0],cursor[2],cursor[4],0), Bmaxc = opencv_core.cvScalar(cursor[1],cursor[3],cursor[5],0);
		CvScalar Gminc = opencv_core.cvScalar(leftclick[0],leftclick[2],leftclick[4],0), Gmaxc = opencv_core.cvScalar(leftclick[1],leftclick[3],leftclick[5],0);
		*/
		CvArr mask;
		
		int widthratio=(wscreen/320)+1;
		int heightratio=(hscreen/240)+1;
		int temp;
		temp=wscreen+hscreen;
		
		
		
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
		
		
				while(flag)
				{	
					CvScalar Bminc = opencv_core.cvScalar(cursor[0],cursor[2],cursor[4],0), Bmaxc = opencv_core.cvScalar(cursor[1],cursor[3],cursor[5],0);
					CvScalar Gminc = opencv_core.cvScalar(leftclick[0],leftclick[2],leftclick[4],0), Gmaxc = opencv_core.cvScalar(leftclick[1],leftclick[3],leftclick[5],0);

					
					if(temp!=(wscreen+hscreen)){
					widthratio=(wscreen/320)+1;
					heightratio=(hscreen/240)+1;
					temp=wscreen+hscreen;
					System.out.println("\n"+widthratio+"\t"+heightratio);
					}
					
					
						
					img1 = opencv_highgui.cvQueryFrame(capture1);
					//IplImage img1 = IplImage.create(img2.height(), img2.width(), img2.depth(), img2.nChannels());
					
					opencv_core.cvFlip(img1, img1, 1 );
					if(img1 == null){
						System.err.println("No Image");
						break;
						}
					
					//only detect blue color 1,0
					imgbinB = ccmFilterSet.Filter(img1,imghsv,imgbinB,Bmaxc, Bminc, contour1, contour2, storage,moments,1,0,widthratio,heightratio);
					//only detect blue color 0,1
					imgbinG = ccmFilterSet.Filter(img1,imghsv,imgbinG,Gmaxc, Gminc, contour1, contour2, storage,moments,0,1,widthratio,heightratio);
					
					//imgC will store both imgbinB and imgbinG
					opencv_core.cvOr(imgbinB,imgbinG,imgC,mask=null);
					
					Image imageToShow = Utils.mat2Image(img1);
					updateImageView(original, imageToShow);
					opencv_highgui.cvShowImage("Combined",imgC);	
					opencv_highgui.cvShowImage("Original",img1);
					
					//cvSaveImage("C:/Users/Dhiraj/Documents/Eclipse-Workspace/MouseEye/_createdimg/Original.jpg",img1);
					//original.setStyle("-fx-background-image: url('/_createdimg/Original.jpg');");
					//original.setImage("\_createdimg\Original.jpg");	
					
					
					char c = (char)opencv_highgui.cvWaitKey(30);
					if(c=='q') break;
						
					}
				if (!flag)
				{
					cvDestroyWindow("Combined");
					cvDestroyWindow("Original");
					cvReleaseCapture(capture1);
				}
	}
	
	
	private void updateImageView(ImageView view, Image image)
	{
		Utils.onFXThread(view.imageProperty(), image);
	}



	public void stopCamera() {
		// TODO Auto-generated method stub
		
		flag = false;
		
	}



	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		resolution.setItems(list);
		
		cslider1.setValue(cursor[0]);
		cslider1stop.setValue(cursor[1]);
		cslider2.setValue(cursor[2]);
		cslider2stop.setValue(cursor[3]);
		cslider3.setValue(cursor[4]);
		cslider3stop.setValue(cursor[5]);
		
		lslider1.setValue(leftclick[0]);
		lslider1stop.setValue(leftclick[1]);
		lslider2.setValue(leftclick[2]);
		lslider2stop.setValue(leftclick[3]);
		lslider3.setValue(leftclick[4]);
		lslider3stop.setValue(leftclick[5]);
		
		
		cslider1.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {              
                    cursor[0]=new_val.intValue();
            }
        });
		
		cslider1stop.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {              
                    cursor[1]=new_val.intValue();
            }
        });
		
		cslider2.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {              
                    cursor[2]=new_val.intValue();
            }
        });
		
		cslider2stop.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {              
                    cursor[3]=new_val.intValue();
            }
        });
		
		cslider3.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {              
                    cursor[4]=new_val.intValue();
            }
        });
		
		cslider3stop.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {              
                    cursor[5]=new_val.intValue();
            }
        });
		
		lslider1.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {              
            	leftclick[0]=new_val.intValue();
            }
        });
		
		lslider1stop.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {              
            	leftclick[1]=new_val.intValue();
            }
        });
		
		lslider2.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {              
            	leftclick[2]=new_val.intValue();
            }
        });
		
		lslider2stop.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {              
            	leftclick[3]=new_val.intValue();
            }
        });
		
		lslider3.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {              
            	leftclick[4]=new_val.intValue();
            }
        });
		
		lslider3stop.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {              
                    leftclick[5]=new_val.intValue();
            }
        });
		
		
		
		
		
		/*double cslidervalue[] = null,lslidervalue[] = null;
		for(int j=0;j<6;j++)
		{
			cslidervalue[j]=cursor[j];
			lslidervalue[j]=leftclick[j];
		}
		
		for(int i=0;i<6;i++)
		{
		 cslidervalue[0]=cslider1.getValue();
		 cslidervalue[1]=cslider1stop.getValue();
		 cslidervalue[2]=cslider2.getValue();
		 cslidervalue[3]=cslider2stop.getValue();
		 cslidervalue[4]=cslider3.getValue();
		 cslidervalue[5]=cslider3stop.getValue();
		 lslidervalue[0]=lslider1.getValue();
		 lslidervalue[1]=lslider1stop.getValue();
		 lslidervalue[2]=lslider2.getValue();
		 lslidervalue[3]=lslider2stop.getValue();
		 lslidervalue[4]=lslider3.getValue();
		 lslidervalue[5]=lslider3stop.getValue();
		}	
		for(int i=0;i<4;i++)
		{
		System.out.println(cslidervalue[i]+"\t");
		System.out.println(cslidervalue[i+1]+"\n");
		}
		for(int i=0;i<4;i++)
		{
		System.out.println(lslidervalue[i]+"\t");
		System.out.println(lslidervalue[i+1]+"\n");
		}
*/
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		wscreen = gd.getDisplayMode().getWidth();
		hscreen = gd.getDisplayMode().getHeight();
		switch(hscreen){
		//"1280 x 720","1920 x 1080","2560 x 1440","3840 x 2160","7680 x 4320"
		case 720:
			resolution.getSelectionModel().select(0);
			wscreen=1280;
			hscreen=720;
			break;
		case 1080:
			resolution.getSelectionModel().select(1);
			wscreen=1920;
			hscreen=1080;
			break;
		case 1440:
			resolution.getSelectionModel().select(2);
			wscreen=2560;
			hscreen=1440;
			break;
		case 2160:
			resolution.getSelectionModel().select(3);
			wscreen=3840;
			hscreen=2160;
			break;
		case 4320:
			resolution.getSelectionModel().select(4);
			wscreen=7680;
			hscreen=4320;
			break;	
		default: 
			break;
		}
		System.out.println("wid"+wscreen);
		System.out.println("hei"+hscreen);
		
	}
	
	
	public void resolutionChanged(ActionEvent event) {
		String tempresolution=resolution.getValue();
		
		switch(tempresolution){
		case "1280 x 720":
			wscreen=1280;
			hscreen=720;
			break;
		case "1920 x 1080":
			wscreen=1920;
			hscreen=1080;
			break;
		case "2560 x 1440":
			wscreen=2560;
			hscreen=1440;
			break;
		case "3840 x 2160":
			wscreen=3840;
			hscreen=2160;
			break;
		case "7680 x 4320":
			wscreen=7680;
			hscreen=4320;
			break;	
		default: 
			break;
		}
		System.out.println(wscreen);
		System.out.println(hscreen);
		
	}
	
	/*
	 public void onslidersChanged(ActionEvent event) {
		double cslidervalue[] = null,lslidervalue[] = null;
		for(int j=0;j<6;j++)
		{
			cslidervalue[j]=cursor[j];
			lslidervalue[j]=leftclick[j];
		}
		
		for(int i=0;i<6;i++)
		{
		 cslidervalue[0]=cslider1.getValue();
		 cslidervalue[1]=cslider1stop.getValue();
		 cslidervalue[2]=cslider2.getValue();
		 cslidervalue[3]=cslider2stop.getValue();
		 cslidervalue[4]=cslider3.getValue();
		 cslidervalue[5]=cslider3stop.getValue();
		 lslidervalue[0]=lslider1.getValue();
		 lslidervalue[1]=lslider1stop.getValue();
		 lslidervalue[2]=lslider2.getValue();
		 lslidervalue[3]=lslider2stop.getValue();
		 lslidervalue[4]=lslider3.getValue();
		 lslidervalue[5]=lslider3stop.getValue();
		}	
		for(int i=0;i<4;i++)
		{
		System.out.println(cslidervalue[i]+"\t");
		System.out.println(cslidervalue[i+1]+"\n");
		}
		for(int i=0;i<4;i++)
		{
		System.out.println(lslidervalue[i]+"\t");
		System.out.println(lslidervalue[i+1]+"\n");
		}


		
	}*/
	
}
