create table recorrencia (
    id                 bigint generated always as identity,
    descricao          varchar(200) not null,
    valor              numeric(15, 2) not null,
    tipo               varchar(10)  not null,
    carteira_id        bigint       not null,
    categoria_id       bigint       not null,
    forma_pagamento_id bigint,
    frequencia         varchar(10)  not null,
    dia_vencimento     smallint,
    data_inicio        date         not null,
    data_fim           date,
    ativa              boolean      not null default true,
    criado_em          timestamptz  not null,
    criado_por         varchar(100) not null,
    atualizado_em      timestamptz,
    atualizado_por     varchar(100),
    constraint pk_recorrencia primary key (id),
    constraint fk_recorrencia_carteira foreign key (carteira_id) references carteira (id),
    constraint fk_recorrencia_categoria foreign key (categoria_id) references categoria (id),
    constraint fk_recorrencia_forma_pagamento foreign key (forma_pagamento_id) references forma_pagamento (id)
);

create index ix_recorrencia_ativa on recorrencia (ativa);

alter table lancamento add column recorrencia_id bigint;
alter table lancamento add constraint fk_lancamento_recorrencia
    foreign key (recorrencia_id) references recorrencia (id);
