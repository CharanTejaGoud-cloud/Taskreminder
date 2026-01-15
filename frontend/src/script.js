const API_URL = "http://localhost:8080/tasks";

function addTask() {
    const task = {
        title: document.getElementById("title").value,
        description: document.getElementById("description").value,
        email: document.getElementById("email").value,
        dueTimestamp: Number(document.getElementById("dueTimestamp").value),
        status: "PENDING"
    };

    fetch(`${API_URL}/add`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(task)
    })
    .then(res => res.json())
    .then(() => {
        loadTasks();
        alert("Task added successfully");
    });
}

function loadTasks() {
    fetch(`${API_URL}/list`)
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById("taskList");
            list.innerHTML = "";

            data.forEach(task => {
                const li = document.createElement("li");

                li.innerHTML = `
                    <b>${task.title}</b> - ${task.status}
                    <button onclick="deleteTask(${task.id})">❌ Delete</button>
                `;

                list.appendChild(li);
            });
        });
}

function deleteTask(id) {
    if (!confirm("Are you sure you want to delete this task?")) {
        return;
    }

    fetch(`${API_URL}/${id}`, {
        method: "DELETE"
    })
    .then(() => {
        loadTasks();
        alert("Task deleted successfully");
    });
}

loadTasks();
