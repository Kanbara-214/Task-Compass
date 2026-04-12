create table if not exists app_users (
    id bigserial primary key,
    display_name varchar(80) not null,
    email varchar(160) not null,
    password_hash varchar(120) not null,
    created_at timestamp not null
);

create unique index if not exists uk_app_users_email on app_users (email);

create table if not exists tasks (
    id bigserial primary key,
    owner_id bigint not null,
    title varchar(160) not null,
    description varchar(2000),
    due_date date not null,
    importance integer not null,
    urgency integer not null,
    estimated_minutes integer not null,
    status varchar(20) not null,
    category varchar(80) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_tasks_owner foreign key (owner_id) references app_users (id),
    constraint chk_tasks_importance check (importance between 1 and 5),
    constraint chk_tasks_urgency check (urgency between 1 and 5),
    constraint chk_tasks_estimated_minutes check (estimated_minutes between 15 and 720)
);

create index if not exists idx_tasks_owner_updated_at on tasks (owner_id, updated_at);
create index if not exists idx_tasks_owner_due_date on tasks (owner_id, due_date);
