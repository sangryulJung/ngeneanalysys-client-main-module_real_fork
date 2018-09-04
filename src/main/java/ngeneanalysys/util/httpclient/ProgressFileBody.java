package ngeneanalysys.util.httpclient;

import ngeneanalysys.task.FileUploadTask;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.Args;

import java.io.*;

public class ProgressFileBody extends FileBody {
    private FileUploadTask fileUploadTask;

    public ProgressFileBody(final File file, FileUploadTask progressTask) {
        super(file);
        this.fileUploadTask = progressTask;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        Args.notNull(out, "Output stream");
        final InputStream in = new FileInputStream(super.getFile());
        try {
            final byte[] tmp = new byte[4096];
            int l;
            long remaining = this.getContentLength();
            while ((l = in.read(tmp)) != -1) {
                out.write(tmp, 0, l);
                remaining -= l;
                fileUploadTask.updateProgress(getContentLength() - remaining, getContentLength());
            }
            out.flush();
        } finally {
            in.close();
        }
    }
}
