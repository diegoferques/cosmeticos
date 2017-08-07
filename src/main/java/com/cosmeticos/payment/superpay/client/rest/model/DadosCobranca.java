package com.cosmeticos.payment.superpay.client.rest.model;

import java.util.List;

public class DadosCobranca {
    private int codigoCliente;
    private int tipoCliente;
    private String nome;
    private String email;
    private String dataNascimento;
    private String sexo;
    private String documento;
    private String documento2;
    private Endereco endereco;
    private List<Telefone> telefone;

    public int getCodigoCliente() { return codigoCliente; }
    public int getTipoCliente() { return tipoCliente; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getDataNascimento() { return dataNascimento; }
    public String getSexo() { return sexo; }
    public String getDocumento() { return documento; }
    public String getDocumento2() { return documento2; }
    public Endereco getEndereco() { return endereco; }
    public List<Telefone> getTelefone() { return telefone; }

    public void setCodigoCliente(int codigoCliente) { this.codigoCliente = codigoCliente; }
    public void setTipoCliente(int tipoCliente) { this.tipoCliente = tipoCliente; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setDocumento(String documento) { this.documento = documento; }
    public void setDocumento2(String documento2) { this.documento2 = documento2; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
    public void setTelefone(List<Telefone> telefone) { this.telefone = telefone; }
}
