package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listMessages(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "list";
    }
    @GetMapping("/add")
    public String newMessage(Model model) {
        model.addAttribute("message", new Message());
        return "form";
    }
      @PostMapping("/process")
    public String processForm(@Valid Message message, BindingResult result) {
        if (result.hasErrors()) {
            return "messageform";
        }
        messageRepository.save(message);
        return "redirect:/";
    }
    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model) {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }
    @RequestMapping("/delete/{id}")
    public String delMessage(@PathVariable("id") long id) {
        messageRepository.deleteById(id);
        return "redirect:/";
    }
    @PostMapping("/add")
    public String processMessage(@ModelAttribute Message message,
                               @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/add";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            message.setContent(uploadResult.get("url").toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }
}
