package dev.nj.task_mgt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagementSystemApplication.class, args);
	}

}
// TODO-01: GET /api/tasks endpoint
// TODO-02: tests POST /api/accounts endpoint
// TODO-03: tests GET /api/tasks endpoint