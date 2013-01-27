package dropwizard_test_drive.resources

import static javax.ws.rs.core.MediaType.*

import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path

import org.apache.commons.io.FileUtils

import com.jayway.jaxrs.hateoas.Linkable
import com.sun.jersey.core.header.FormDataContentDisposition
import com.sun.jersey.multipart.FormDataParam

@Path('/file')
class FileResource {

    @POST
    @Path('/upload')
    @Linkable('fileUpload.post')
    @Consumes(MULTIPART_FORM_DATA)
    String uploadFile(@FormDataParam("file") InputStream uploadStream, @FormDataParam("file") FormDataContentDisposition fileDetail) {
        File uploadedFile = new File("/tmp/${fileDetail.fileName}")

        FileUtils.copyInputStreamToFile(uploadStream, uploadedFile)

        "File uploaded to: ${uploadedFile}"
    }
}
