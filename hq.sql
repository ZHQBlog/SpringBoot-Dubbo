/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2022/4/10 17:48:09                           */
/*==============================================================*/


drop table if exists product;

drop table if exists product_desc;

drop table if exists product_type;

/*==============================================================*/
/* Table: product                                               */
/*==============================================================*/
create table product
(
   id                   bigint not null auto_increment,
   name                 varchar(12) not null,
   prive                bigint not null,
   sale_price           bigint not null,
   sale_point           varchar(64) not null,
   image                varchar(256) not null,
   stock                bigint not null,
   flag                 tinyint(1) not null,
   create_time          datetime not null,
   update_time          datetime not null,
   create_user          bigint not null,
   update_user          bigint not null,
   primary key (id)
);

/*==============================================================*/
/* Table: product_desc                                          */
/*==============================================================*/
create table product_desc
(
   id                   bigint not null auto_increment,
   p_desc               text not null,
   product_id           bigint not null,
   primary key (id)
);

/*==============================================================*/
/* Table: product_type                                          */
/*==============================================================*/
create table product_type
(
   id                   bigint not null auto_increment,
   pid                  bigint not null,
   name                 varchar(12) not null,
   primary key (id)
);

