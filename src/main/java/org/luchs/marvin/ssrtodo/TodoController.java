package org.luchs.marvin.ssrtodo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Controller
public class TodoController {

    @Autowired
    private TodoListService todoListService;

    @GetMapping("/")
    public ModelAndView get(TaskForm taskForm) {
        return renderTodoList();
    }

    @GetMapping("/tasks")
    public ModelAndView getTasks() {
        return redirectToTasks();
    }

    @GetMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getTasksJson() {
        return ResponseEntity.ok(todoList.getTasks());
    }

    @PostMapping(value = "/tasks", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView processTaskForm(@Valid TaskForm taskForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView model = new ModelAndView();
            model.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
            return renderTodoList(model);
        }

        todoListService.addTask(taskForm.getDescription(), taskForm.getDueDate());
        return redirectToTasks();
    }

    @PostMapping(value = "/tasks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addTaskJson(@Valid @RequestBody TaskForm taskForm) {
        addTask(taskForm);
        return ResponseEntity
            .created(URI.create("/tasks"))
            .build();
    }

    @GetMapping("/tasks/completed")
    public ModelAndView getCompletedTasks() {
        return redirectToTasks();
    }

    @PostMapping(value = "/tasks/completed", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView processCompleteTasksForm(@RequestParam("completedTasks[]") List<String> taskIds) {
        todoListService.completeTasks(taskIds);
        return redirectToTasks();
    }

    @PostMapping(value = "/tasks/completed", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity completeTasksJson(@RequestBody CompletedTasksForm completedTasksForm) {
        completeTasks(completedTasksForm.getCompletedTasks());
        return ResponseEntity
            .status(HttpStatus.SEE_OTHER)
            .location(URI.create("/tasks"))
            .build();
    }

    private ModelAndView redirectToTasks() {
        return new ModelAndView("redirect:/");
    }

    private ModelAndView renderTodoList() {
        return renderTodoList(new ModelAndView());
    }

    private ModelAndView renderTodoList(ModelAndView model) {
        model.setViewName("todoList");
        model.addObject("tasks", todoListService.getTasks());
        return model;
    }

}