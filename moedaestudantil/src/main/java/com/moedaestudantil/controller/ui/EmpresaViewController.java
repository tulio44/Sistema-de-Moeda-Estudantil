package com.moedaestudantil.controller.ui;

import com.moedaestudantil.domain.model.Empresa;
import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.repo.EmpresaRepository;
import com.moedaestudantil.domain.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ui/empresas")
public class EmpresaViewController {

  @Autowired
  private EmpresaRepository empresaRepository;

  @Autowired
  private UserRepository userRepository;

  @GetMapping
  public String listar(Model model,
                       @RequestParam(value = "erro", required = false) String erro) {
    model.addAttribute("empresas", empresaRepository.findAll());
    model.addAttribute("erro", erro);
    return "empresas/list";
  }

  @GetMapping("/novo")
  public String novo(Model model) {
    model.addAttribute("empresa", new Empresa());
    return "empresas/form";
  }

  @GetMapping("/{id}/editar")
  public String editar(@PathVariable Long id, Model model) {
    Empresa e = empresaRepository.findById(id).orElseThrow();
    model.addAttribute("empresa", e);
    return "empresas/form";
  }

  @PostMapping("/salvar")
  public String salvar(@ModelAttribute Empresa empresa,
                       @RequestParam(name="userId", required=false) Long userId,
                       RedirectAttributes ra) {
    // Vincula o User obrigatório
    if (userId != null) {
      User u = userRepository.findById(userId).orElse(null);
      if (u != null) empresa.setUser(u);
    }
    if (empresa.getUser() == null) {
      ra.addFlashAttribute("msgErro", "Informe um usuário válido (userId).");
      ra.addFlashAttribute("empresa", empresa);
      return "redirect:/ui/empresas/novo";
    }

    empresaRepository.save(empresa);
    ra.addFlashAttribute("msgOk", "Empresa salva com sucesso.");
    return "redirect:/ui/empresas";
  }

  @PostMapping("/{id}/excluir")
  public String excluir(@PathVariable Long id, RedirectAttributes ra) {
    try {
      empresaRepository.deleteById(id);
      ra.addFlashAttribute("msgOk", "Empresa excluída.");
    } catch (DataIntegrityViolationException e) {
      ra.addFlashAttribute("erro",
          "Não é possível excluir: há registros vinculados (ex.: Vantagem).");
    }
    return "redirect:/ui/empresas";
  }
}
