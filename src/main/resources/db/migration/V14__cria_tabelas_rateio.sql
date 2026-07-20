create table rateio (
    id             bigint generated always as identity,
    lancamento_id  bigint       not null,
    tipo           varchar(15)  not null,
    criado_em      timestamptz  not null,
    criado_por     varchar(100) not null,
    atualizado_em  timestamptz,
    atualizado_por varchar(100),
    constraint pk_rateio primary key (id),
    constraint uk_rateio_lancamento unique (lancamento_id),
    constraint fk_rateio_lancamento foreign key (lancamento_id) references lancamento (id)
);

create table participante_rateio (
    id             bigint generated always as identity,
    rateio_id      bigint       not null,
    usuario_id     bigint       not null,
    percentual     numeric(7, 4),
    valor          numeric(15, 2),
    criado_em      timestamptz  not null,
    criado_por     varchar(100) not null,
    atualizado_em  timestamptz,
    atualizado_por varchar(100),
    constraint pk_participante_rateio primary key (id),
    constraint fk_participante_rateio_rateio foreign key (rateio_id) references rateio (id) on delete cascade,
    constraint fk_participante_rateio_usuario foreign key (usuario_id) references usuario (id)
);

create index ix_participante_rateio_rateio on participante_rateio (rateio_id);
