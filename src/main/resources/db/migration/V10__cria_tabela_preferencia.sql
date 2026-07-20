create table preferencia (
    id             bigint generated always as identity,
    chave          varchar(40)  not null,
    valor          varchar(120) not null,
    usuario_id     bigint,
    criado_em      timestamptz  not null,
    criado_por     varchar(100) not null,
    atualizado_em  timestamptz,
    atualizado_por varchar(100),
    constraint pk_preferencia primary key (id),
    constraint fk_preferencia_usuario foreign key (usuario_id) references usuario (id),
    constraint uk_preferencia_chave_usuario unique (chave, usuario_id)
);
