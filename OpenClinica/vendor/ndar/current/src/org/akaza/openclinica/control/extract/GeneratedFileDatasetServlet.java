/**
 * 
 */
package org.akaza.openclinica.control.extract;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class GeneratedFileDatasetServlet extends HttpServlet {

	/**
	 * @param request
	 * @param response
	 */
	private void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String path = (String) request.getAttribute("generate");
		System.out.println("file path found at " + path);
		if (path != null) {
			ServletOutputStream sos = null;
			BufferedOutputStream bos = null;
			InputStream is = null;
			BufferedInputStream bis = null;
			try {
				// commented out since this should be set before this servlet is called: response.setContentType("text/plain");
				// response.setHeader("Content-disposition",
				// "attachment; filename=\"" + path + "\"");
				sos = response.getOutputStream();

				bos = new BufferedOutputStream(sos);
				java.io.File local = new java.io.File(path);
				is = new FileInputStream(local);
				bis = new BufferedInputStream(is);
				int length = (int) local.length();
				int bytesRead;
				byte[] buff = new byte[length];

				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					bos.write(buff, 0, bytesRead);
				}

			} catch (Exception ee) {
				ee.printStackTrace();
				sos.print("Error streaming data! :" + ee.getMessage());
			} finally {
				if (bis != null) {
					bis.close();
				}
				if (is != null) {
					is.close();
				}
				if (bos != null) {
					bos.close();
				}
				if (sos != null) {
					sos.flush();
					sos.close();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    try {
	        process(request, response);
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    try {
	        process(request, response);
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	}

}
