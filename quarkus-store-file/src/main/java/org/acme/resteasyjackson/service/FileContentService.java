package org.acme.resteasyjackson.service;

import org.acme.resteasyjackson.model.Base64Input;
import org.acme.resteasyjackson.model.FileContent;
import org.acme.resteasyjackson.util.Base64Coder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class FileContentService {

    @Inject
    EntityManager em;

    public List<FileContent> getAllFiles() {
        return FileContent.listAll();
    }

    @Transactional
    public FileContent saveFile(Base64Input input) {
        FileContent fileContent = new FileContent();
        fileContent.fileName = input.fileName;
        fileContent.data = Base64Coder.decodeToByteArray(input.content);
        fileContent.persist();

        //System.out.println("Filecontent ID within Transaction: " + fileContent.getId());
        return fileContent;
    }
}
