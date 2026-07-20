create table carteira (
    id            bigint generated always as identity,
    nome          varchar(120)  not null,
    tipo          varchar(20)   not null,
    dono_id       bigint,
    saldo_inicial numeric(15, 2) not null default 0,
    moeda         varchar(3)    not null default 'BRL',
    cor           varchar(20),
    icone         varchar(40),
    ativa         boolean       not null default true,
    criado_em     timestamptz   not null,
    criado_por    varchar(100)  not null,
    atualizado_em timestamptz,
    atualizado_por varchar(100),
    constraint pk_carteira primary key (id),
    constraint fk_carteira_dono foreign key (dono_id) references usuario (id)
);

create index ix_carteira_dono on carteira (dono_id);
create index ix_carteira_tipo on carteira (tipo);
