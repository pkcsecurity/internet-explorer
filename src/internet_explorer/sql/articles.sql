-- :name insert-article
-- :command :returning-execute
-- :result :one
insert into articles (title, content)
values (:title, :content) returning *;

-- :name article-by-id
-- :result :one
select * from articles
where article_id = :id;

-- :name all-articles
select * from articles
order by date_added asc;

-- :name delete-article
-- :command :execute
delete from articles where article_id = :id;
