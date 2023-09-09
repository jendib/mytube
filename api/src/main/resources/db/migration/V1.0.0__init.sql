create table video
(
    id uuid not null primary key,
    title varchar(2000) not null,
    youtubeId varchar(2000) not null,
    description text,
    channelId varchar(2000) not null,
    channelTitle varchar(2000) not null,
    publishedDate timestamp not null,
    viewCount integer,
    likeCount integer,
    duration numeric not null,
    thumbnailUrl varchar(2000) not null
);