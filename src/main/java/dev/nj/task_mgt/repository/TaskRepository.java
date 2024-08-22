package dev.nj.task_mgt.repository;

import dev.nj.task_mgt.entities.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findAllOrderByCreatedDesc();

    List<Task> findByAuthorIgnoreCase(String author);
}
