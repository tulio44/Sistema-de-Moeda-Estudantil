package com.moedaestudantil.controller.ui;

import com.moedaestudantil.domain.model.*;
import com.moedaestudantil.domain.model.enums.TipoTransacao;
import com.moedaestudantil.domain.repo.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Controller
@RequestMapping("/ui/vantagens")
public class VantagemController {

    private final VantagemRepository vantagemRepo;
    private final UserRepository userRepo;
    private final EmpresaRepository empresaRepo;
    private final AlunoRepository alunoRepo;
    private final TransacaoRepository transacaoRepo;

    public VantagemController(VantagemRepository vantagemRepo,
                              UserRepository userRepo,
                              EmpresaRepository empresaRepo,
                              AlunoRepository alunoRepo,
                              TransacaoRepository transacaoRepo) {
        this.vantagemRepo = vantagemRepo;
        this.userRepo = userRepo;
        this.empresaRepo = empresaRepo;
        this.alunoRepo = alunoRepo;
        this.transacaoRepo = transacaoRepo;
    }

    // ================== ÁREA EMPRESA ==================

    @GetMapping
    public String listarDaEmpresa(Model model) {
        Empresa empresa = getEmpresaLogada();
        model.addAttribute("vantagens", vantagemRepo.findByEmpresa(empresa));
        return "vantagens/list";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("vantagem", new Vantagem());
        return "vantagens/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Empresa empresa = getEmpresaLogada();
        Vantagem v = vantagemRepo.findById(id).orElse(null);
        if (v == null || !v.getEmpresa().getId().equals(empresa.getId())) {
            ra.addFlashAttribute("erro", "Vantagem não encontrada.");
            return "redirect:/ui/vantagens";
        }
        model.addAttribute("vantagem", v);
        return "vantagens/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Vantagem vantagem,
                         @RequestParam(value = "imagemArquivo", required = false) MultipartFile imagemArquivo,
                         RedirectAttributes ra) {

        Empresa empresa = getEmpresaLogada();

        Vantagem alvo;
        if (vantagem.getId() == null) {
            alvo = new Vantagem();
            alvo.setEmpresa(empresa);
            alvo.setAtivo(true);
        } else {
            alvo = vantagemRepo.findById(vantagem.getId()).orElse(null);
            if (alvo == null || !alvo.getEmpresa().getId().equals(empresa.getId())) {
                ra.addFlashAttribute("erro", "Vantagem inválida.");
                return "redirect:/ui/vantagens";
            }
        }

        alvo.setTitulo(vantagem.getTitulo());
        alvo.setDescricao(vantagem.getDescricao());
        alvo.setCusto(vantagem.getCusto());
        alvo.setAtivo(vantagem.getAtivo() != null ? vantagem.getAtivo() : true);

        if (imagemArquivo != null && !imagemArquivo.isEmpty()) {
            try {
                String base64 = Base64.getEncoder().encodeToString(imagemArquivo.getBytes());
                alvo.setFotoUrl(base64);
            } catch (IOException e) {
                ra.addFlashAttribute("erro", "Erro ao processar imagem.");
                return "redirect:/ui/vantagens";
            }
        }

        vantagemRepo.save(alvo);
        ra.addFlashAttribute("msgOk", "Vantagem salva com sucesso.");
        return "redirect:/ui/vantagens";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        Empresa empresa = getEmpresaLogada();
        Vantagem v = vantagemRepo.findById(id).orElse(null);
        if (v == null || !v.getEmpresa().getId().equals(empresa.getId())) {
            ra.addFlashAttribute("erro", "Vantagem inválida.");
            return "redirect:/ui/vantagens";
        }
        vantagemRepo.delete(v);
        ra.addFlashAttribute("msgOk", "Vantagem excluída.");
        return "redirect:/ui/vantagens";
    }

    // ================== ÁREA ALUNO ==================

    @GetMapping("/disponiveis")
    public String listarDisponiveis(Model model) {
        model.addAttribute("vantagens", vantagemRepo.findByAtivoTrue());
        return "vantagens/disponiveis";
    }

    @PostMapping("/{id}/resgatar")
    public String resgatar(@PathVariable Long id,
                           RedirectAttributes ra) {

        // 1. Vantagem válida
        Vantagem v = vantagemRepo.findById(id).orElse(null);
        if (v == null || Boolean.FALSE.equals(v.getAtivo())) {
            ra.addFlashAttribute("erro", "Vantagem inválida ou inativa.");
            return "redirect:/ui/vantagens/disponiveis";
        }

        // 2. Usuário logado -> Aluno
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User u = userRepo.findByEmail(email).orElse(null);
        if (u == null) {
            ra.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/ui/vantagens/disponiveis";
        }

        Aluno aluno = alunoRepo.findByUser(u).orElse(null);
        if (aluno == null) {
            ra.addFlashAttribute("erro", "Nenhum aluno vinculado a este usuário.");
            return "redirect:/ui/vantagens/disponiveis";
        }

        // 3. Verificar saldo
        int custo = v.getCusto();
        int saldoAtual = aluno.getSaldo() != null ? aluno.getSaldo() : 0;

        if (saldoAtual < custo) {
            ra.addFlashAttribute("erro", "Saldo insuficiente. Você tem " + saldoAtual + " moedas.");
            return "redirect:/ui/vantagens/disponiveis";
        }

        // 4. Debitar saldo
        aluno.setSaldo(saldoAtual - custo);
        alunoRepo.save(aluno);

        // 5. Registrar transação com cupom
        String codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Transacao tx = new Transacao();
        tx.setTipo(TipoTransacao.RESGATE_ALUNO);
        tx.setDestinoAluno(aluno);
        tx.setVantagem(v);
        tx.setQuantidade(custo);
        tx.setCodigoCupom(codigo);
        tx.setMensagem("Resgate da vantagem: " + v.getTitulo());
        transacaoRepo.save(tx);

        // 6. Mensagem
        ra.addFlashAttribute("msgOk",
                "Vantagem '" + v.getTitulo() + "' resgatada com sucesso! Código do cupom: " + codigo);

        return "redirect:/ui/vantagens/disponiveis";
    }

    // ================== HELPER ==================

    private Empresa getEmpresaLogada() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado."));
        return empresaRepo.findByUser(u)
                .orElseThrow(() -> new IllegalStateException("Nenhuma empresa vinculada a este usuário."));
    }
}
