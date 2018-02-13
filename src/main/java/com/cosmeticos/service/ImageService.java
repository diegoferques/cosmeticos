package com.cosmeticos.service;

import com.cosmeticos.model.Image;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.ImageRepository;
import com.cosmeticos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 01/08/2017..
 */
@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserRepository userRepository;

    public void create(Image vote) {
        imageRepository.save(vote);
    }

    public Optional<Image> find(Long id){
        return Optional.ofNullable(imageRepository.findOne(id));
    }

    public void delete(Long id) {

        Image i = imageRepository.findOne(id);

        User user = userRepository.findOne(i.getUser().getIdLogin());

        if(user != null ) {

            user.getImageCollection().remove(i);
            userRepository.save(user);
            imageRepository.delete(i);
            imageRepository.flush();
        }
        else
        {
            throw new IllegalArgumentException(id + " nao possui User");
        }
    }

    public List<Image> findAll(Image image) {
        return imageRepository.findAll(Example.of(image));
    }
}
