package it.zero11.xroads.utils.modules.core.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {
	private byte[] body;

    public MultiReadHttpServletRequest(HttpServletRequest request) {
        super(request);
        try {
            body = IOUtils.toByteArray(request.getInputStream());
        } catch (IOException ex) {
            body = new byte[0];
        }
    }

    public byte[] getBody() {
		return body;
	}

	@Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStream() {
            ByteArrayInputStream bais = new ByteArrayInputStream(body);

            @Override
            public int read() throws IOException {
                return bais.read();
            }

			@Override
			public boolean isFinished() {
				return bais.available() == 0;
			}

			@Override
			public boolean isReady() {
				return bais.available() > 0;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				
			}
        };
    }
}