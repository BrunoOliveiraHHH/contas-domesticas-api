create table categoria (
    id               bigint generated always as identity,
    nome             varchar(120) not null,
    tipo             varchar(20)  not null,
    categoria_pai_id bigint,
    cor              varchar(20),
    icone            varchar(40),
    ativa            boolean      not null default true,
    criado_em        timestamptz  not null,
    criado_por       varchar(100) not null,
    atualizado_em    timestamptz,
    atualizado_por   varchar(100),
    constraint pk_categoria primary key (id),
    constraint fk_categoria_pai foreign key (categoria_pai_id) references categoria (id)
);

create index ix_categoria_tipo on categoria (tipo);
create index ix_categoria_pai on categoria (categoria_pai_id);
