package org.dataverse.apachetest.web.servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/testServlet/*")
public class TestServlet extends HttpServlet {

    
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        StringBuilder textOutputStringBuilder = new StringBuilder();
        int streamBytesCount = 0;
        String pathInfo = httpServletRequest.getPathInfo();
        if ((pathInfo != null)) {
            textOutputStringBuilder.append(pathInfo).append("\n");
            String[] pathInfoComponents = pathInfo.split("/");
            if ((pathInfoComponents.length >= 4) &&
                    ((pathInfoComponents[1] != null) && pathInfoComponents[1].equals("download")) &&
                    ((pathInfoComponents[2] != null) && pathInfoComponents[2].equals("streamBytes")) &&
                    (pathInfoComponents[3] != null)
                    ) {
                streamBytesCount = Integer.parseInt(pathInfoComponents[3]);
            }
        }
        textOutputStringBuilder.append("streamBytesCount=").append(streamBytesCount).append("\n");
        if (streamBytesCount == 0) {
            sendTexttResponse(httpServletResponse, textOutputStringBuilder);
        } else {
            sendByteStream(httpServletResponse, streamBytesCount);
        }
    }


    private void sendTexttResponse(HttpServletResponse httpServletResponse, StringBuilder textOutputStringBuilder) throws IOException {
        textOutputStringBuilder.append("**** END OF OUTPUT ****");

        httpServletResponse.setContentType("text/plain");
        PrintWriter printWriter = httpServletResponse.getWriter();
        printWriter.println(textOutputStringBuilder.toString());
        printWriter.flush();
        printWriter.close();
    }


    private void sendByteStream(HttpServletResponse httpServletResponse, int streamBytesCount) throws IOException {
        byte[] dataBuffer = new byte[8192];

        Random random = new Random(System.currentTimeMillis());
        httpServletResponse.setContentType("application/octet-stream");

        try (ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream()) {
            int totalBytesWritten = 0;
            while (totalBytesWritten < streamBytesCount) {
                random.nextBytes(dataBuffer);
                int bytesToWrite = dataBuffer.length;
                if ((totalBytesWritten + bytesToWrite) > streamBytesCount) {
                    bytesToWrite = streamBytesCount - totalBytesWritten;
                }
                servletOutputStream.write(dataBuffer, 0, bytesToWrite);
                totalBytesWritten += bytesToWrite;
                servletOutputStream.flush();
            }
        }

    }


}