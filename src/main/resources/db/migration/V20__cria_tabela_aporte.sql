create table aporte (
    id              bigint generated always as identity,
    investimento_id bigint       not null,
    valor           numeric(15, 2) not null,
    data            date         not null,
    tipo            varchar(10)  not null,
    criado_em       timestamptz  not null,
    criado_por      varchar(100) not null,
    atualizado_em   timestamptz,
    atualizado_por  varchar(100),
    constraint pk_aporte primary key (id),
    constraint fk_aporte_investimento foreign key (investimento_id) references investimento (id) on delete cascade
);

create index ix_aporte_investimento_data on aporte (investimento_id, data);
