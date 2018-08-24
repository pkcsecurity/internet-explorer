-- TODO document migrations, and make it clear that you can't modify old
-- migrations

-- pgcrypto gives us cryptographically random ID's via gen_random_uuid().
-- Random ID's are not strictly necessary, but access control / IDOR issues
-- tend to be very common in web applications, and random ID's make them
-- less practical to exploit.
create extension if not exists pgcrypto;

create table users (
  user_id uuid default gen_random_uuid() primary key,
  password_hash text not null,
  full_name text not null,
  email varchar(255) not null unique,
  is_admin boolean not null default false,
  date_added timestamp default now()
);

-- TODO get rid of these default auth values
-- probably want a cli to add a user
insert into users (password_hash, full_name, email, is_admin)
values ('bcrypt+sha512$674761338e588e8a1865b0aa67f793e0$12$f0288a89775751019d543bb78c5d8faa20d43f82fb7e7f5e',
        'Non Admin', 'nonadmin@example.com', false),
  ('bcrypt+sha512$674761338e588e8a1865b0aa67f793e0$12$f0288a89775751019d543bb78c5d8faa20d43f82fb7e7f5e',
   'Admin', 'admin@example.com', true);

create table sessions (
  session_id varchar(255) not null primary key,
  user_id uuid not null references users on delete cascade,
  started timestamp default now(),
  last_active timestamp default now()
);

create table articles (
  article_id bigserial primary key,
  title text not null,
  content text not null,
  date_added timestamp default now()
);

insert into articles (title, content) values
  ('great article', 'actually this article doesn`t say much'),
  ('better article', 'just kidding :p');
