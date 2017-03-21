package application;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgproc.CvMoments;

//color contour moment filter
public class ccmFilterSet {
	//temp  variable
	public static int t;

	public static IplImage Filter(IplImage img, IplImage imghsv,IplImage imgBin,
			CvScalar maxc, CvScalar minc, 
			CvSeq contour1,CvSeq contour2, CvMemStorage storage,CvMoments moments,
			int b,int g,int widthratio,int heightratio) throws AWTException{
		
		double moment10, moment01, areaMax, areaC=0,m_area;
		int posX=0,posY=0;

		
		
		opencv_imgproc.cvCvtColor(img,imghsv,opencv_imgproc.CV_BGR2HSV);
		

		
		opencv_core.cvInRangeS(imghsv,minc,maxc,imgBin);
		
		areaMax= 1000;
		
		opencv_imgproc.cvFindContours(imgBin,storage,contour1,Loader.sizeof(CvContour.class),
				opencv_imgproc.CV_RETR_LIST,opencv_imgproc.CV_LINK_RUNS,opencv_core.cvPoint(0,0));
	
		contour2= contour1;
		
		while(contour1 != null && !contour1.isNull())
		{
			areaC = opencv_imgproc.cvContourArea(contour1,opencv_core.CV_WHOLE_SEQ,1);
		
			if(areaC>areaMax)
				areaMax = areaC;
		
			contour1 = contour1.h_next();
		
		}
	
		while(contour2 !=null && !contour2.isNull())
		{
			areaC= opencv_imgproc.cvContourArea(contour2,opencv_core.CV_WHOLE_SEQ,1);
		
			if(areaC<areaMax)
			{
				opencv_core.cvDrawContours(imgBin,contour2,opencv_core.CV_RGB(0,0,0),opencv_core.CV_RGB(0,0,0),
						0,opencv_core.CV_FILLED,8,opencv_core.cvPoint(0,0));
			}
		
			contour2=contour2.h_next();
		}
		
		opencv_imgproc.cvMoments(imgBin, moments, 1);

		moment10 = opencv_imgproc.cvGetSpatialMoment(moments, 1, 0);
		moment01 = opencv_imgproc.cvGetSpatialMoment(moments, 0, 1);
		m_area = opencv_imgproc.cvGetCentralMoment(moments, 0, 0);
		
		
		
	
		return imgBin;
	}

}

