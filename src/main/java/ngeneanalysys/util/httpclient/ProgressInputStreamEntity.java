package ngeneanalysys.util.httpclient;

import ngeneanalysys.task.FileUploadTask;
import ngeneanalysys.util.LoggerUtil;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.Args;
import org.slf4j.Logger;

import java.io.*;

public class ProgressInputStreamEntity extends InputStreamEntity {
    private FileUploadTask fileUploadTask;

    public ProgressInputStreamEntity(InputStream instream, long length, ContentType contentType, FileUploadTask progressTask) {
        super(instream, length, contentType);
        this.fileUploadTask = progressTask;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        final InputStream instream = this.getContent();
        try {
            final byte[] buffer = new byte[OUTPUT_BUFFER_SIZE];
            int l;
            // consume no more than length
            long remaining = this.getContentLength();
            while (remaining > 0) {
                l = instream.read(buffer, 0, (int)Math.min(OUTPUT_BUFFER_SIZE, remaining));
                if (l == -1) {
                    break;
                }
                outstream.write(buffer, 0, l);
                remaining -= l;

                fileUploadTask.updateProgress(getContentLength() - remaining, getContentLength());
            }

        } finally {
            instream.close();
        }
    }
}
